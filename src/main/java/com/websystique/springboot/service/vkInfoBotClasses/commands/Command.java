package com.websystique.springboot.service.vkInfoBotClasses.commands;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Data
@RequiredArgsConstructor
public class Command {
    private final String commandNameRegex; //regex возможных имен команды
    private final int numberOfArgs;
    private final String[] argsRegex;
    private final String basisSqlQuery;
    private String[] args;

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

    public String getSqlQuery() {
        return formSqlQuery(basisSqlQuery, args);
    }

    private String formSqlQuery(String basisSqlQuery, String... args) {
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
        return String.format(basisSqlQuery, argsForSqlQuery);
    }

}
