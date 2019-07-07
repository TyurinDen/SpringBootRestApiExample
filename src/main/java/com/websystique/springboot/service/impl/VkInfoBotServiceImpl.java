package com.websystique.springboot.service.impl;

import com.google.gson.*;
import com.squareup.okhttp.*;
import com.websystique.springboot.configs.VkInfoBotConfig;
import com.websystique.springboot.repositories.TestClientCustomRepository;
import com.websystique.springboot.service.VkInfoBotService;
import com.websystique.springboot.service.vkInfoBotClasses.LongPollServer;
import com.websystique.springboot.service.vkInfoBotClasses.commands.*;
import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import com.websystique.springboot.service.vkInfoBotClasses.errors.ResponseFromApiVk;
import com.websystique.springboot.service.vkInfoBotClasses.exceptions.*;
import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;
import com.websystique.springboot.service.vkInfoBotClasses.messages.NewEvent;
import com.websystique.springboot.service.vkInfoBotClasses.messages.NewEventsArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Component
public class VkInfoBotServiceImpl implements VkInfoBotService {
    private static Logger logger = LoggerFactory.getLogger(VkInfoBotServiceImpl.class.getName());
    private final String SQL_QUERY = "SELECT \n" +
            "    cl.client_id,\n" +
            "    cl.last_name,\n" +
            "    cl.first_name,\n" +
            "    cl.email,\n" +
            "    cl.phone_number,\n" +
            "    cl.city,\n" +
            "    cl.country,\n" +
            "    cl.client_description_comment,\n" +
            "    cl.comment,\n" +
            "    cl.client_state,\n" +
            "    cl.postpone_comment,\n" +
            "    cl.birth_date,\n" +
            "    u.last_name AS Owner_lastName,\n" +
            "    u.first_name AS Owner_name,\n" +
            "    u1.last_name AS Mentor_lastName,\n" +
            "    u1.first_name AS Mentor_name\n" +
            "FROM\n" +
            "    CLIENT cl\n" +
            "        LEFT JOIN\n" +
            "    user u ON cl.owner_user_id = u.user_id\n" +
            "        LEFT JOIN\n" +
            "    user u1 ON cl.owner_mentor_id = u1.user_id\n" +
            "WHERE %s LIMIT 5";
    private final String VK_URL_PARAM_GROUP_ID = "group_id";
    private final String VK_URL_PARAM_ACCESS_TOKEN = "access_token";
    private final String VK_URL_PARAM_API_VERSION = "v";
    private final String VK_URL_PARAM_PEER_ID = "peer_id";
    private final String VK_URL_API;
    private final String VK_API_VERSION;
    private final String VK_BOT_ACCESS_TOKEN;
    private final String VK_BOT_CONFIRMATION_TOKEN;
    private final String VK_BOT_CLUB_ID;
    private final OkHttpClient okHttpClient; //TODO Как реализовать http-клиент? Как статик? Инжектить извне?
    private TestClientCustomRepository clientCustomRepository;
    private final VkInfoBotConfig botConfig;

    private Set<Command> commandSet;
    private LongPollServer longPollServer;
    private int ts; //topic start??

    @Autowired
    public VkInfoBotServiceImpl(VkInfoBotConfig vkInfoBotConfig,
                                @Qualifier("TestClientCustomRepositoryImpl") TestClientCustomRepository clientCustomRepository) {
        Command findById = new Command("^i$|^и$|^ид$|^id$", 1,
                new String[]{"^[0-9]+\\*?$|^\\*[0-9]+$"}, SQL_QUERY + "CLIENT_ID RLIKE('%s')");
        Command findByCityAndLastName = new Command("^cl$|^cln$|^сл$|^слн$", 2,
                new String[]{"^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$", "^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$"},
                SQL_QUERY + "CITY RLIKE('%s') AND LAST_NAME RLIKE('%s')");
        commandSet = new HashSet<>(Arrays.asList(findById, findByCityAndLastName));
        this.clientCustomRepository = clientCustomRepository;

        okHttpClient = new OkHttpClient();
        botConfig = vkInfoBotConfig;

        VK_URL_API = botConfig.getVkApiUrl();
        VK_API_VERSION = botConfig.getVkApiVersion();
        VK_BOT_ACCESS_TOKEN = botConfig.getVkInfoBotAccessToken();
        VK_BOT_CONFIRMATION_TOKEN = botConfig.getVkInfoBotConfirmationToken();
        VK_BOT_CLUB_ID = botConfig.getVkInfoBotClubId();
    }

    @Override
    public String getConfirmationToken() {
        return VK_BOT_CONFIRMATION_TOKEN;
    }

    @Override
    public void sendResponseMessage(Message message, String clients) {

    }

    @Override
    public String findClients(String messageText) {
        Command command = getCommand(messageText.trim().toLowerCase());
        if (command != null) {
            List<Object[]> clientList = clientCustomRepository.getClients(command.getSqlQuery());
            return formTextViewOfClientList(clientList);
        } else {
            return "";
        }
    }

    private Command getCommand(String messageText) {
        for (Command command : commandSet) {
            if (command.checkSyntax(messageText)) {
                return command;
            }
        }
        return null;
    }

    private String formTextViewOfClientList(List<Object[]> clients) {
        String[] prefixes = new String[]{"ID", "Фамилия", "Имя", "Email", "Телефон", "Город", "Страна", "Описание", "Комментарий",
                "Состояние", "Отложенный комментарий", "Дата рождения", "Менеджер", "Ментор"};
        StringBuilder stringBuilder = new StringBuilder();
        for (Object[] objects : clients) {
            stringBuilder.append("************************** CLIENT **************************\n");
            for (int i = 0; i < 12; i++) {
                if (objects[i] != null) {
                    stringBuilder.append(" ").append(prefixes[i]).append(": ").append(objects[i]).append("\n");
                } else {
                    stringBuilder.append(" ").append(prefixes[i]).append(": ").append("Не указано").append("\n");
                }
            }
            for (int i = 12, j = 12; i < 16; i += 2, j++) {
                if (objects[i] != null) {
                    stringBuilder.append(" ").append(prefixes[j]).append(": ").append(objects[i]).append(" ").
                            append(objects[i + 1]).append("\n");
                } else {
                    stringBuilder.append(" ").append(prefixes[j]).append(": ").append("Не указано").append("\n");
                }
            }
        }
        return stringBuilder.toString();
    }
    //на запрос будет выдано не более пяти результатов
    //
    //formTextViewOfClientsList(List<Object[]> objectsList)
    //sendErrorMessage(Message message)
    //sendHelpMessage(Message message)
    //
    //sendMessage(Message message)

    public void sendMessage(Message message) throws UnableToSendMessageException {
        HttpUrl.Builder urlBuilder;
        String responseBodyString;
        ResponseFromApiVk response;

        try {
            urlBuilder = HttpUrl.parse(VK_URL_API + "/messages.send").newBuilder();
            urlBuilder.addQueryParameter("user_id", String.valueOf(message.getFromId()));
            //urlBuilder.addQueryParameter("random_id", String.valueOf(message.getRandomId())); //TODO ??????
            //urlBuilder.addQueryParameter(VK_URL_PARAM_PEER_ID, String.valueOf(message.getPeerId()));
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

    private void fillOutMessagesQueue(Map<Message, Future<List<TestClient>>> mapOfRunningCommands, Queue<Message> outMessages) {
        for (Message message : mapOfRunningCommands.keySet()) {
            Future<List<TestClient>> clientFuture = mapOfRunningCommands.get(message);
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
