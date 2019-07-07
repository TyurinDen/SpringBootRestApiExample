package com.websystique.springboot.service;

import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;

public interface VkInfoBotService {

    String findClients(String messageText);

    String getConfirmationToken();

    void sendResponseMessage(Message message, String clients);

}
