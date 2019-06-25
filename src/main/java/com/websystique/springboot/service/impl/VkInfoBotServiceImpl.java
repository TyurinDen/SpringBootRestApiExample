package com.websystique.springboot.service.impl;

import com.google.gson.*;
import com.squareup.okhttp.*;
import com.websystique.springboot.configs.InfoBotConfig;
import com.websystique.springboot.service.VkInfoBotService;
import com.websystique.springboot.service.vkInfoBotClasses.LongPollServer;
import com.websystique.springboot.service.vkInfoBotClasses.commands.*;
import com.websystique.springboot.service.vkInfoBotClasses.entities.Client;
import com.websystique.springboot.service.vkInfoBotClasses.errors.ResponseFromApiVk;
import com.websystique.springboot.service.vkInfoBotClasses.exceptions.*;
import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;
import com.websystique.springboot.service.vkInfoBotClasses.messages.NewEvent;
import com.websystique.springboot.service.vkInfoBotClasses.messages.NewEventsArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

@Component
public class VkInfoBotServiceImpl implements VkInfoBotService {
    private static Logger logger = LoggerFactory.getLogger(VkInfoBotServiceImpl.class.getName());
    private final String VK_URL_PARAM_GROUP_ID = "group_id";
    private final String VK_URL_PARAM_ACCESS_TOKEN = "access_token";
    private final String VK_URL_PARAM_API_VERSION = "v";
    private final String VK_URL_PARAM_PEER_ID = "peer_id";
    private final String VK_URL_API;
    private final String VK_API_VERSION;
    private final String VK_BOT_ACCESS_TOKEN;
    private final String VK_BOT_CLUB_ID;

    private final OkHttpClient okHttpClient; //TODO Как реализовать http-клиент? Как статик? Инжектить извне?
    private final InfoBotConfig botConfig;
    // очереди сообщений создаются один раз
    // TODO: 22.05.2019 надо ли ограничивать размер очереди?
    private final Queue<Message> inMessagesQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Message> outMessagesQueue = new ConcurrentLinkedQueue<>();
    // коллекция выполняющихся команд создается один раз
    private final Map<Message, Future<List<Client>>> mapOfRunningCommands = new ConcurrentHashMap<>(); //карта выполняющихся команд
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private CommandExecutor commandExecutor;
    private EntityManager entityManager;  // TODO: 15.06.2019 будет автосвязываться
    private LongPollServer longPollServer;
    private int ts; //topic start??

    @Autowired
    public VkInfoBotServiceImpl(InfoBotConfig infoBotConfig) {
        okHttpClient = new OkHttpClient();
        botConfig = infoBotConfig;

        VK_URL_API = botConfig.getVkApiUrl();
        VK_API_VERSION = botConfig.getVkApiVersion();
        VK_BOT_ACCESS_TOKEN = botConfig.getVkInfoBotAccessToken();
        VK_BOT_CLUB_ID = botConfig.getVkInfoBotClubId();

        entityManager = new EntityManager(); // TODO: 15.06.2019 будет автосвязываться
        initInfoBot();
    }

    @Override
    public void getUpdatesAndFillIncomingMsgQueue() {
        NewEventsArray newEventsArray = getUpdates();
        markIncomingMessagesAsRead(newEventsArray);
        fillIncomingMessageQueue(newEventsArray, inMessagesQueue);
    }

    @Override
    public void executeCommands() {
        runCommandQueue(inMessagesQueue, mapOfRunningCommands, outMessagesQueue);
    }

    @Override
    public void sendResponseMessages() {
        fillOutMessagesQueue(mapOfRunningCommands, outMessagesQueue);
        sendOutMessages(outMessagesQueue); //TODO метод должен бросать исключения. Пусть летит Спрингу или перехватывать?
    }

    private void initInfoBot() {
        Command findClientsById = new Command("^i$|^и$|^ид$|^id$", 1,
                new String[]{"^[0-9]+\\*?$|^\\*[0-9]+$"},
                "SELECT * FROM CLIENT WHERE CLIENT_ID RLIKE('%s');");
        Command findClientByCityAndLastname = new Command("^cl$|^cln$|^сл$|^слн$", 2,
                new String[]{"^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$", "^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$"},
                "SELECT * FROM CLIENT WHERE CITY RLIKE('%s') AND LAST_NAME RLIKE('%s');");

        commandExecutor = new CommandExecutor(Arrays.asList(findClientsById, findClientByCityAndLastname),
                entityManager, executorService);
    }

    private NewEventsArray getUpdates() throws UnableToGetUpdatesException {
        String responseBodyString = "";

        try {
            if (longPollServer == null) {
                longPollServer = getLongPollServer();
                ts = longPollServer.getTs();
            }

            HttpUrl.Builder urlBuilder = HttpUrl.parse(longPollServer.getServer()).newBuilder();
            urlBuilder.addQueryParameter("act", "a_check");
            urlBuilder.addQueryParameter("key", longPollServer.getKey());
            urlBuilder.addQueryParameter("ts", String.valueOf(ts));
            //TODO  разобраться! работает, но если увеличить время, то падает в http клиенте
            urlBuilder.addQueryParameter("wait", "2");

            responseBodyString = getResponseBodyFromHttpRequest(urlBuilder.build().toString());
            NewEventsArray newEventsArray = createJavaObjFromWholeJson(responseBodyString, NewEventsArray.class);
            ts = newEventsArray.getTs();
            return newEventsArray;
        } catch (UnableToGetLPSException e) {
            throw new UnableToGetUpdatesException("Unable to get new events! LPS data not received", e);
        } catch (NoSuchObjectFoundException e) {
            longPollServer = null; // получен неадекватный ответ, возможно перезапрос ЛПС поможет
            throw new UnableToGetUpdatesException("Unable to get new events! Server response: " + responseBodyString, e);
        } catch (IOException e) {
            throw new UnableToGetUpdatesException("Unable to get new events! It looks like the network is down", e);
        } catch (NullPointerException e) {
            throw new UnableToGetUpdatesException("General error! Unable to get new events", e);
        }
    }

    private LongPollServer getLongPollServer() throws UnableToGetLPSException {
        String responseBodyString = "";

        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(VK_URL_API + "/groups.getLongPollServer").newBuilder();
            urlBuilder.addQueryParameter(VK_URL_PARAM_GROUP_ID, VK_BOT_CLUB_ID);
            urlBuilder.addQueryParameter(VK_URL_PARAM_ACCESS_TOKEN, VK_BOT_ACCESS_TOKEN);
            urlBuilder.addQueryParameter(VK_URL_PARAM_API_VERSION, VK_API_VERSION);

            responseBodyString = getResponseBodyFromHttpRequest(urlBuilder.build().toString());
            return createJavaObjFromJsonByName(responseBodyString, "response", LongPollServer.class);
        } catch (IOException e) {
            throw new UnableToGetLPSException("Unable to get LPS! It looks like the network is down", e);
        } catch (NoSuchObjectFoundException e) {
            System.out.println("Something went wrong. Server response: " + responseBodyString); // TODO: 02.06.2019 убрать
            throw new UnableToGetLPSException("Unable to get LPS! Server response: " + responseBodyString, e);
        } catch (NullPointerException e) {
            throw new UnableToGetLPSException("General error! Unable to get LPS!", e);
        }
    }

    private void runCommandQueue(Queue<Message> inMessages, Map<Message, Future<List<Client>>> mapOfRunningCommands,
                                 Queue<Message> outMessages) { //заполнение списка выполняющихся команд
        commandExecutor.executeCommandsList(inMessages, mapOfRunningCommands, outMessages);
    }

    private void fillOutMessagesQueue(Map<Message, Future<List<Client>>> mapOfRunningCommands, Queue<Message> outMessages) {
        for (Message message : mapOfRunningCommands.keySet()) {
            Future<List<Client>> clientFuture = mapOfRunningCommands.get(message);
            if (clientFuture.isDone()) {
                try {
                    //message.setText(formStringViewOfClientList(clientFuture.get()));
                    message.setText(String.valueOf(clientFuture.get()));
                } catch (InterruptedException | ExecutionException e) { //TODO исключения!! Как правильно сделать?
                    e.printStackTrace();
                }
                outMessages.offer(message);
                mapOfRunningCommands.remove(message);
            }
        }
    }

    private String formStringViewOfClientList(List<Client> clients) {
        return null;
    }

    private void sendOutMessages(Queue<Message> outMsgQueue) {
        Message message;
        while ((message = outMsgQueue.poll()) != null) {
            try {
                sendMessage(message);
            } catch (UnableToSendMessageException e) { //TODO надо ли ловить или пусть летит дальше?
                e.printStackTrace();
            }
        }
    }

    private void markIncomingMessagesAsRead(NewEventsArray updatesArray) throws UnableToSendMarkAsReadException {
        HttpUrl.Builder urlBuilder;
        String responseBodyString;
        ResponseFromApiVk response;

        try {
            for (NewEvent updatesObject : updatesArray.getNewEventsList()) {
                urlBuilder = HttpUrl.parse(VK_URL_API + "/messages.markAsRead").newBuilder();
                urlBuilder.addQueryParameter(VK_URL_PARAM_PEER_ID, String.valueOf(updatesObject.getMessage().getPeerId()));
                urlBuilder.addQueryParameter(VK_URL_PARAM_GROUP_ID, String.valueOf(updatesObject.getGroupId()));
                urlBuilder.addQueryParameter(VK_URL_PARAM_ACCESS_TOKEN, botConfig.getVkInfoBotAccessToken());
                urlBuilder.addQueryParameter(VK_URL_PARAM_API_VERSION, botConfig.getVkApiVersion());

                responseBodyString = getResponseBodyFromHttpRequest(urlBuilder.build().toString());
                response = createJavaObjFromWholeJson(responseBodyString, ResponseFromApiVk.class);
                if (response.getResponse() != 1) {
                    throw new UnableToSendMarkAsReadException("Unable to send markAsRead! Server response: "
                            + responseBodyString);
                }
                System.out.println(responseBodyString); // TODO: 11.06.2019 убрать
            }
        } catch (IOException e) {
            throw new UnableToSendMarkAsReadException("Unable to send markAsRead! It looks like the Network is down", e);
        } catch (NoSuchObjectFoundException e) {
            throw new UnableToSendMarkAsReadException("Unable to send markAsRead!", e);
        } catch (NullPointerException e) {
            throw new UnableToSendMarkAsReadException("General error! Unable to send markAsRead", e);
        }
    }

    private void fillIncomingMessageQueue(NewEventsArray newEventsArray, Queue<Message> inMessages) {
        for (NewEvent newEvent : newEventsArray.getNewEventsList()) {
            inMessages.offer(newEvent.getMessage());
        }
    }

    public void sendMessage(Message message) throws UnableToSendMessageException {
        HttpUrl.Builder urlBuilder;
        String responseBodyString;
        ResponseFromApiVk response;

        try {
            urlBuilder = HttpUrl.parse(VK_URL_API + "/messages.send").newBuilder();
            urlBuilder.addQueryParameter("user_id", String.valueOf(message.getFromId()));
            urlBuilder.addQueryParameter("random_id", String.valueOf(message.getRandomId())); //TODO ??????
            urlBuilder.addQueryParameter(VK_URL_PARAM_PEER_ID, String.valueOf(message.getPeerId()));
            urlBuilder.addQueryParameter(VK_URL_PARAM_GROUP_ID, String.valueOf(VK_BOT_CLUB_ID));
            urlBuilder.addQueryParameter("reply_to", String.valueOf(message.getId()));
            urlBuilder.addQueryParameter("message", String.valueOf(message.getText()));
            urlBuilder.addQueryParameter(VK_URL_PARAM_ACCESS_TOKEN, VK_BOT_ACCESS_TOKEN);
            urlBuilder.addQueryParameter(VK_URL_PARAM_API_VERSION, VK_API_VERSION);

            responseBodyString = getResponseBodyFromHttpRequest(urlBuilder.build().toString());
            response = createJavaObjFromWholeJson(responseBodyString, ResponseFromApiVk.class);
            if (response == null) {
                //if (response.getResponse() != 1) {
                throw new UnableToSendMessageException("Unable to send message! Server response: " + responseBodyString);
            }
            System.out.println(responseBodyString); // TODO: 12.06.2019 remove
        } catch (IOException e) {
            throw new UnableToSendMessageException("Unable to send message! It looks like the Network is down");
        } catch (NoSuchObjectFoundException e) {
            throw new UnableToSendMessageException("Unable to send message!", e);
        } catch (NullPointerException e) {
            throw new UnableToSendMessageException("General error! Unable to send message!", e);
        }

    }

    private <T> T createJavaObjFromJsonByName(String jsonString, String jsonObjectName, Class<T> tClass) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonParser parser = new JsonParser();
        JsonElement rootJsonElements;

        try {
            rootJsonElements = parser.parse(jsonString);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new NoSuchObjectFoundException(String.format("Parsing JSON failed. Object name: '%s'. JSON: '%s'",
                    jsonObjectName, jsonString), e);
        }

        for (Map.Entry<String, JsonElement> jsonMapEntryElement : rootJsonElements.getAsJsonObject().entrySet()) {
            if (jsonMapEntryElement.getKey().equals(jsonObjectName)) {
                return gson.fromJson(jsonMapEntryElement.getValue().toString(), tClass);
            }
        }
        throw new NoSuchObjectFoundException(String.format("Object with name '%s' is not found in JSON: '%s'",
                jsonObjectName, jsonString));
    }

    private <T> T createJavaObjFromWholeJson(String jsonString, Class<T> tClass) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonParser parser = new JsonParser();
        JsonElement rootJsonElements;

        try {
            rootJsonElements = parser.parse(jsonString);
        } catch (JsonIOException | JsonSyntaxException e) {
            throw new NoSuchObjectFoundException(String.format("Parsing JSON failed. JSON: '%s'", jsonString), e);
        }

        return gson.fromJson(rootJsonElements.getAsJsonObject(), tClass);
    }

    private String getResponseBodyFromHttpRequest(String url) throws IOException {
        Response response = okHttpClient.newCall(new Request.Builder().url(url).build()).execute();
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return "";
        }
        String s = responseBody.string();
        System.out.println(s); // TODO: 30.05.2019 убрать
        return s;
//        return responseBody.string();
    }

}
