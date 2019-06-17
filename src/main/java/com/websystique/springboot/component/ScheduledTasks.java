package com.websystique.springboot.component;

import com.websystique.springboot.service.VkInfoBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTasks {

    @Autowired
    private VkInfoBotService vkInfoBotService;

    @Scheduled(fixedRate = 2_000)
    private void checkNewEvents() {
        vkInfoBotService.getUpdatesAndFillIncomingMsgQueue();
    }

    @Scheduled(fixedRate = 2_000)
    private void executeCommands() {
        vkInfoBotService.executeCommands();
    }

    @Scheduled(fixedRate = 2_000)
    private void sendResponseMessages() {
        vkInfoBotService.sendResponseMessages();
    }

}
