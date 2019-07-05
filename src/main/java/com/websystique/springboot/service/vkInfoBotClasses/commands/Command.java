package com.websystique.springboot.service.vkInfoBotClasses.commands;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Component
public class Command {
    private String commandNameRegex; //regex возможных имен команды
    private int numberOfArgs;
    private String[] argsRegex;
    private String queryBasisString;
    private String[] args;
    private String queryString;

    public Command() {
    }

    public Command(String commandNameRegex, int numberOfArgs, String[] argsRegex, String queryBasisString) {
        this.commandNameRegex = commandNameRegex;
        this.numberOfArgs = numberOfArgs;
        this.argsRegex = argsRegex;
        this.queryBasisString = queryBasisString;
    }

    public Future<List<TestClient>> execute(EntityManager entityManager, ExecutorService executorService) {
        //queryString = String.format(queryBasisString, args);
        System.out.println(formSqlQueryString(queryBasisString, args));
        return executorService.submit(() -> entityManager.getResultList(queryBasisString));

//        Query query = entityManager.createNativeQuery(queryString);
//        return query.getResultList();

    }

    public boolean checkSyntax(String messageText) {
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

    public String formSqlQueryString(String queryBasisString, String... args) {
        String[] argsForSqlQuery = new String[numberOfArgs];
        for (int i = 0; i < numberOfArgs; i++) {
            int index = args[i].indexOf('*');
            if (index < 0) {
                argsForSqlQuery[i] = '^' + args[i] + '$';
            } else if (index == 0) {
                argsForSqlQuery[i] = args[i].substring(1) + '$';
            } else {
                argsForSqlQuery[i] = '^' + args[i].substring(0, index);
            }
        }
        return String.format(queryBasisString, argsForSqlQuery);
    }

}
