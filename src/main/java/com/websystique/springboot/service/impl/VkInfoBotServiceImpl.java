package com.websystique.springboot.service.impl;

import com.google.gson.*;
import com.squareup.okhttp.*;
import com.websystique.springboot.configs.VkInfoBotConfig;
import com.websystique.springboot.repositories.TestClientCustomRepository;
import com.websystique.springboot.service.VkInfoBotService;
import com.websystique.springboot.service.vkInfoBotClasses.LongPollServer;
import com.websystique.springboot.service.vkInfoBotClasses.commands.Command;
import com.websystique.springboot.service.vkInfoBotClasses.errors.ResponseFromApiVk;
import com.websystique.springboot.service.vkInfoBotClasses.exceptions.NoSuchObjectFoundException;
import com.websystique.springboot.service.vkInfoBotClasses.exceptions.UnableToSendMarkAsReadException;
import com.websystique.springboot.service.vkInfoBotClasses.exceptions.UnableToSendMessageException;
import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;
import com.websystique.springboot.service.vkInfoBotClasses.messages.NewEvent;
import com.websystique.springboot.service.vkInfoBotClasses.messages.NewEventsArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final VkInfoBotConfig botConfig;
    private final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
    private TestClientCustomRepository clientCustomRepository;
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
    public void sendResponseMessage(Message message, String responseMessageText) {

    }

    @Override
    public String findClients(String messageText) {
        Command command = getCommand(messageText.trim().toLowerCase());
        if (command != null) {
            List<Object[]> clientList = clientCustomRepository.getClients(command.getSqlQuery());
            if (clientList.isEmpty()) {
                return "Ничего не найдено"; // TODO: 10.07.2019 переделать строки в константы
            }
            return formTextViewOfClientList(clientList);
        } else {
            return "Команда некорректна";
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

    public void sendMessage(Message message) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(VK_URL_API + "/messages.send").newBuilder();
        urlBuilder.addQueryParameter("user_id", String.valueOf(message.getFromId()));
        //urlBuilder.addQueryParameter("random_id", String.valueOf(message.getRandomId())); //TODO ??????
        //urlBuilder.addQueryParameter(VK_URL_PARAM_PEER_ID, String.valueOf(message.getPeerId()));
        urlBuilder.addQueryParameter(VK_URL_PARAM_GROUP_ID, String.valueOf(VK_BOT_CLUB_ID));
        urlBuilder.addQueryParameter("reply_to", String.valueOf(message.getId()));
        urlBuilder.addQueryParameter("message", String.valueOf(message.getText()));
        urlBuilder.addQueryParameter(VK_URL_PARAM_ACCESS_TOKEN, VK_BOT_ACCESS_TOKEN);
        urlBuilder.addQueryParameter(VK_URL_PARAM_API_VERSION, VK_API_VERSION);

        String url = urlBuilder.build().toString();
        RequestBody requestBody = RequestBody.create(TEXT, message.getText());
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    logger.error("Unable to send message! Server response: " + responseBody.string());
                }
                logger.error("Unable to send message! Server response code: " + response.code());
            }
        } catch (IOException e) {
            logger.error("Unable to send message! It looks like the Network is down", e);
        }

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
}
