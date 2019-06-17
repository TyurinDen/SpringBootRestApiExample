package com.websystique.springboot.service;

public interface VkInfoBotService {

    void getUpdatesAndFillIncomingMsgQueue();
    void executeCommands();
    void sendResponseMessages();

}
