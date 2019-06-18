package com.websystique.springboot.service.vkInfoBotClasses.commands;

import com.websystique.springboot.service.vkInfoBotClasses.entities.Client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Command {
    private String commandNameRegex; //regex возможных имен команды
    private int numberOfArgs;
    private String[] argsRegex;
    private String[] args;
    private String queryBasisString;
    private String queryString;

    public Command(String commandNameRegex, int numberOfArgs, String queryBasisString, String... argsRegex) {
        this.commandNameRegex = commandNameRegex;
        this.numberOfArgs = numberOfArgs;
        this.queryBasisString = queryBasisString;
        this.argsRegex = argsRegex;
    }

    public boolean validateCommand(String messageText) {
        String[] commandComponents = messageText.split(" ");
        if (commandComponents.length < numberOfArgs + 1) {
            return false;
        }
        if (!commandComponents[0].matches(commandNameRegex)) {
            return false;
        }

        args = Arrays.copyOfRange(commandComponents, 1, numberOfArgs + 1);
        for (int i = 0; i < args.length; i++) {
            if (!args[i].matches(argsRegex[i])) {
                return false;
            }
        }
        return true;

    }
/* Чтобы команда была универсальна, надо придумать как аргументы преобразовать в параметры запроса.
 * То есть каждой команде надо переделать код, который бы список аргументов превращал в параметры запроса
 * и возвращал сформированный запрос. Передавать код можно лямбдами, то есть получается, что при создании
 * каждой команды надо будет прописывать код, который из аргументов сформирует параметры запроса.
 */
    public Future<List<Client>> execute(EntityManager entityManager, ExecutorService executorService) {
        queryString = String.format(queryBasisString, args);
        System.out.println(queryString);
        return executorService.submit(() -> entityManager.getResultList(queryBasisString));

//        Query query = entityManager.createNativeQuery(queryString);
//        return query.getResultList();

    }

}
