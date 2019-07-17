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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final MediaType MEDIA_TYPE_PLAINTEXT = MediaType.parse("text/plain; charset=utf-8");
    private TestClientCustomRepository clientCustomRepository;
    private Set<Command> commandSet;

    @Autowired
    public VkInfoBotServiceImpl(VkInfoBotConfig vkInfoBotConfig,
                                @Qualifier("TestClientCustomRepositoryImpl") TestClientCustomRepository clientCustomRepository) {
        Command findById = new Command("^i$|^и$|^ид$|^id$", 1,
                new String[]{"^[0-9]+\\*?$|^\\*[0-9]+$"}, SQL_QUERY + "cl.client_id RLIKE('%s') LIMIT %s");
        Command findByCityAndLastName = new Command("^cln$|^слн$", 2,
                new String[]{"^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$", "^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$"},
                SQL_QUERY + "cl.city RLIKE('%s') AND cl.last_name RLIKE('%s') LIMIT %s");
        Command findByCity = new Command("^c$|^city$|^с$|^сити$|^город$", 1,
                new String[]{"^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$"}, SQL_QUERY + "cl.city RLIKE('%s') LIMIT %s");
        Command findByLastName = new Command("^ln$|^lastname$|^фамилия$|^лн$", 1,
                new String[]{"^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$"}, SQL_QUERY + "cl.last_name RLIKE('%s') LIMIT %s");
        commandSet = new HashSet<>(Arrays.asList(findById, findByCityAndLastName, findByCity, findByLastName));

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
    public void sendResponseMessage(Message message, Iterable<String> clientsList) {
        // TODO: 17.07.2019 упаковать сообщения в сообщения побольше
        List<String> packedClientList = packClientList(clientsList);
        for (String client : packedClientList) {
            message.setText(client);
            sendMessage(message);
        }
    }

    private List<String> packClientList(Iterable<String> clientsList) {
        StringBuilder listItem = new StringBuilder();
        final int MAX_MESSAGE_LENGTH = 4096;
        int messageLength = 0;
        List<String> packedClientList = new ArrayList<>();

        for (String client : clientsList) {
            if (messageLength + client.getBytes().length < MAX_MESSAGE_LENGTH) {
                listItem.append(client);
                messageLength += client.getBytes().length;
            } else {
                packedClientList.add(listItem.toString());
                listItem = new StringBuilder();
                listItem.append(client);
                messageLength = client.getBytes().length;
            }
        }
        packedClientList.add(listItem.toString());
        return packedClientList;
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
    public void sendHelpMessage(Message message) {
        final String HELP_MESSAGE = "*************** VkInfoBot - поиск клиентов в CRM ***************\n\n" +
                " Бот понимает следующие команды (регистр игнорируется):\n" +
                "--------------------------------------------------------------------------------------------------\n" +
                "id [i, ид, и]: поиск по идентефикатору.\n" +
                "id 123: найти клиента с ID равным 123.\n" +
                "id *123: найти клиента с ID, заканчивающимся на 123.\n" +
                "id 123*: найти клиента с ID, начинающимся с 123.\n" +
                "--------------------------------------------------------------------------------------------------\n" +
                "cln [слн]: поиск по городу и фамилии.\n" +
                "cln Москва Иванов: найти клиентов из Москвы с фамилией\n" +
                "Иванов. Или cl м* и*: найти клиентов из города на \'м\'\n" +
                "с фамилией, начинающейся на \'и\'.\n" +
                "--------------------------------------------------------------------------------------------------\n" +
                "city [c, сити, город]: поиск по городу.\n" +
                "--------------------------------------------------------------------------------------------------\n" +
                "ln [lastname, фамилия, лн]: поиск по фамилии.\n" +
                "--------------------------------------------------------------------------------------------------\n" +
                "Все команды поддерживают символ \'*\' в аргументах.\n\n" +
                "Количество результатов ограничено двадцатью, но его\n" +
                "можно уменьшить, передав еще один аргумент команде:\n" +
                "id *123 5 - ограничить вывод пятью результатами.\n";
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
        String[] prefixes = new String[]{"ID:", "ФИО:", "", "Живет:", "Описание:", "Комментарий:",
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
        RequestBody requestBody = new FormEncodingBuilder()
                .add("user_id", String.valueOf(message.getFromId()))
                .add("random_id", String.valueOf(message.getRandomId()))
                .add("group_id", String.valueOf(VK_BOT_CLUB_ID))
                .add("reply_to", String.valueOf(message.getId()))
                .add("message", String.valueOf(message.getText()))
                .add("access_token", VK_BOT_ACCESS_TOKEN)
                .add("v", VK_API_VERSION)
                .build();

        Request request = new Request.Builder().url(VK_URL_API + "/messages.send").post(requestBody).build();
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
