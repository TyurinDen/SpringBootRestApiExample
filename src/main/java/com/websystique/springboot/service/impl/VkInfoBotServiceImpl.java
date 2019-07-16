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
    private final String SQL_QUERY = "SELECT\n" +
            "cl.client_id AS id,\n" +
            "concat(cl.last_name, ' ', cl.first_name) AS fio,\n" +
            "concat('Email: ', cl.email, ', телефон: ', cl.phone_number) AS contacts," +
            "concat(cl.country, ',', ' ', cl.city) AS lives_in,\n" +
            "cl.client_description_comment AS description,\n" +
            "cl.comment,\n" +
            "cl.postpone_comment AS p_comment,\n" +
            "concat(u.last_name, ' ', u.first_name) AS owner,\n" +
            "concat(u1.last_name, ' ', u1.first_name) AS mentor\n" +
            "FROM CLIENT cl\n" +
            "LEFT JOIN\n" +
            "USER u ON cl.owner_user_id = u.user_id\n" +
            "LEFT JOIN\n" +
            "user u1 ON cl.owner_mentor_id = u1.user_id\n" +
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
                new String[]{"^[0-9]+\\*?$|^\\*[0-9]+$"}, SQL_QUERY + "cl.client_id RLIKE('%s') LIMIT %s");
        Command findByCityAndLastName = new Command("^cl$|^cln$|^сл$|^слн$", 2,
                new String[]{"^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$", "^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$"},
                SQL_QUERY + "cl.city RLIKE('%s') AND cl.last_name RLIKE('%s') LIMIT %s");
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
        for (String client : clients) {
            message.setText(client);
            sendMessage(message);
        }
    }

    @Override
    public List<String> findClients(String messageText) {
        final String NOTHING_FOUND = "Ничего не найдено";
        final String INCORRECT_COMMAND = "Команда некорректна";
        Command command = getCommand(messageText.trim().toLowerCase());
        if (command != null) {
            System.out.println(command.getSqlQuery());
            List<Object[]> clientList = clientCustomRepository.getClients(command.getSqlQuery());
            if (clientList.isEmpty()) {
                return Arrays.asList(NOTHING_FOUND);
            }
            return formTextViewOfClientList(clientList);
        } else {
            return Arrays.asList(INCORRECT_COMMAND);
        }
    }

    @Override
    public void sendHelpMessage(Message message) { // TODO: 15.07.2019 дописать справку, чтобы было понятно однозначно
        final String HELP_MESSAGE = ">>>>>>>>>>>>>>>>>>>> VkInfoBot - поиск клиентов в CRM <<<<<<<<<<<<<<<<<<<<\n\n" +
                "Бот понимает следующие команды (регистр символов игнорируется):\n" +
                "id (i, ид, и): поиск по идентефикатору.\n" +
                "id 123: найти клиента с ID == 123.\n" +
                "id *123: найти клиента с ID, заканчивающимся на 123.\n" +
                "id 123*: найти клиента с ID, начинающимся на 123.\n\n" +
                "cl (cln, сл, слн): поиск по городу и фамилии.\n" +
                "cl Москва Иванов: найти клиентов из Москвы с фамилией \n" +
                "Иванов.\n" +
                "cl М* И*: найти клиентов из города, название которого,\n" +
                "начинается на \'м\', с фамилией, начинающейся на \'и\'.\n" +
                "Варианты аргументов команды те же, что и при поиске по\n" +
                "идентефикатору.\n\n" +
                "Количество найденных клиентов ограничено по умолчанию \n" +
                "двадцатью (20), но его можно уменьшить, передав еще один\n" +
                "аргумент команде, например: id *123 5";
        message.setText(HELP_MESSAGE);
        sendMessage(message);
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
        String[] prefixes = new String[]{"ID:", "ФИО:", "", "Живет в:", "Описание:", "Комментарий:",
                "Отложенный комментарий:", "Менеджер:", "Ментор:"};
        List<String> clientsList = new ArrayList<>();
        for (Object[] objects : clients) {
            StringBuilder stringBuilder = new StringBuilder();
            // TODO: 17.07.2019 подумать как красиво вывести первую строку
//            stringBuilder.append("************************** CLIENT **************************\n");
            for (int i = 0; i < prefixes.length; i++) {
                if (objects[i] != null) {
                    stringBuilder.append("> ").append(prefixes[i]).append(" ").append(objects[i]).append("\n");
                } else {
                    stringBuilder.append("> ").append(prefixes[i]).append(" ").append("N/A").append("\n");
                }
            }
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
