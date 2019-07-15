package com.websystique.springboot.service.impl;

import com.squareup.okhttp.*;
import com.websystique.springboot.configs.VkInfoBotConfig;
import com.websystique.springboot.repositories.TestClientCustomRepository;
import com.websystique.springboot.service.VkInfoBotService;
import com.websystique.springboot.service.vkInfoBotClasses.commands.Command;
import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VkInfoBotServiceImpl implements VkInfoBotService {
    private static Logger logger = LoggerFactory.getLogger(VkInfoBotServiceImpl.class.getName());
    private final String SQL_QUERY = "SELECT \n" + // TODO: 12.07.2019 Добавить упорядочивание по фамилии
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
            "WHERE ";
    private final String VK_URL_API;
    private final String VK_API_VERSION;
    private final String VK_BOT_ACCESS_TOKEN;
    private final String VK_BOT_CONFIRMATION_TOKEN;
    private final String VK_BOT_CLUB_ID;
    private final OkHttpClient okHttpClient;
    private final VkInfoBotConfig botConfig;
    private final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
    private TestClientCustomRepository clientCustomRepository;
    private Set<Command> commandSet;

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
    public void sendResponseMessage(Message message, Iterable<String> clients) {
        //TODO каждого найденного клиента отправлять отдельным запросом к АПИ
        for (String client: clients) {
            message.setText(client);
            sendMessage(message);
        }
//        message.setText(responseMessageText);
//        sendMessage(message);
    }

    @Override
    public List<String> findClients(String messageText) {
        Command command = getCommand(messageText.trim().toLowerCase());
        if (command != null) {
            List<Object[]> clientList = clientCustomRepository.getClients(command.getSqlQuery());
            if (clientList.isEmpty()) {
                return Arrays.asList("Ничего не найдено"); // TODO: 10.07.2019 переделать строки в константы
            }
            return formTextViewOfClientList(clientList);
        } else {
            return Arrays.asList("Команда некорректна");
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

    private List<String> formTextViewOfClientList(List<Object[]> clients) {
        String[] prefixes = new String[]{"ID", "Фамилия", "Имя", "Email", "Телефон", "Город", "Страна", "Описание", "Комментарий",
                "Состояние", "Отложенный комментарий", "Дата рождения", "Менеджер", "Ментор"};
        List<String> clientsList = new ArrayList<>();
        for (Object[] objects : clients) {
            StringBuilder stringBuilder = new StringBuilder();
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
            stringBuilder.append("\n\n");
            clientsList.add(stringBuilder.toString());
        }
        return clientsList;
    }

    private void sendMessage(Message message) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(VK_URL_API + "/messages.send").newBuilder();
        urlBuilder.addQueryParameter("user_id", String.valueOf(message.getFromId()));
        urlBuilder.addQueryParameter("random_id", String.valueOf(message.getRandomId()));
        urlBuilder.addQueryParameter("group_id", String.valueOf(VK_BOT_CLUB_ID));
        urlBuilder.addQueryParameter("reply_to", String.valueOf(message.getId()));
        urlBuilder.addQueryParameter("message", String.valueOf(message.getText()));
        urlBuilder.addQueryParameter("access_token", VK_BOT_ACCESS_TOKEN);
        urlBuilder.addQueryParameter("v", VK_API_VERSION);

        String url = urlBuilder.build().toString();
        RequestBody requestBody = RequestBody.create(TEXT, message.getText());
        Request request = new Request.Builder().url(url).post(requestBody).build();
        String responseBodyString = "No response body!";
        try {
            Response response = okHttpClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                responseBodyString = responseBody.string();
                logger.info("Response from API VK: " + responseBodyString);
            }

            if (!response.isSuccessful()) {
                logger.error(String.format("Unable to send message! Server response: %s, response code: %d",
                        responseBodyString, response.code()));
            }
        } catch (Exception e) {
            logger.error("Unable to send message! ", e);
        }

    }

}
