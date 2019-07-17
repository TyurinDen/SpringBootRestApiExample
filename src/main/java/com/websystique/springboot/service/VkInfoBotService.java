package com.websystique.springboot.service;

import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;

public interface VkInfoBotService {

    Iterable<String> findClients(String messageText);

    String getConfirmationToken();

    void sendResponseMessage(Message message, Iterable<String> clientsList);

    void sendHelpMessage(Message message);

}
