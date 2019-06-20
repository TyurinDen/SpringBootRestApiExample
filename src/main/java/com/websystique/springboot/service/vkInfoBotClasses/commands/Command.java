package com.websystique.springboot.service.vkInfoBotClasses.commands;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.websystique.springboot.service.vkInfoBotClasses.entities.Client;

public interface Command {
    boolean checkSyntax(String messageText);
    Future<List<Client>> execute(EntityManager entityManager, ExecutorService executorService);
}
