package com.websystique.springboot.service.vkInfoBotClasses.commands;

import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;

public interface RequestConverter {
    String convert(Command command, Message message);
}
