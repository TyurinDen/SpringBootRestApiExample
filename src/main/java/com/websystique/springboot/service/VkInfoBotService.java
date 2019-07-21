package com.websystique.springboot.service;

import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;

public interface VkInfoBotService {

    String getConfirmationToken();

    void sendResultMessage(Message message);

    void sendHelpMessage(Message message);

}
