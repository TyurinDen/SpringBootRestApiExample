package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.commands.Command;

import java.util.List;

public interface TestClientCustomRepository {

    Iterable<List<Object[]>> getByCommandFromVkBot(String messageText, Command command);

}
