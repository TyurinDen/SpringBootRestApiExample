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
    private final String RESULT_LIMIT_REGEX = "^[1-9]{1,3}$";
    private int resultLimit;
    private final String basisSqlQuery;
    private String sqlQuery;

    public boolean checkSyntax(String messageText) {
        String[] commandComponents = messageText.split(" ");
        if (commandComponents.length < numberOfArgs + 1) {
            return false;
        }
        if (!commandComponents[0].matches(commandNameRegex)) {
            return false;
        }
        if (commandComponents.length > numberOfArgs + 1) {
            if (commandComponents[numberOfArgs + 1].matches(RESULT_LIMIT_REGEX)) {
                resultLimit = Integer.valueOf(commandComponents[numberOfArgs + 1]);
            } else {
                resultLimit = 20; //TODO переделать! считывать параметр из конфиг файла!
            }
        }

        String[] args = Arrays.copyOfRange(commandComponents, 1, numberOfArgs + 1);
        for (int i = 0; i < args.length; i++) {
            if (!args[i].matches(argsRegex[i])) {
                return false;
            }
        }
        sqlQuery = formSqlQuery(basisSqlQuery, args);
        return true;
    }

    public String getSqlQuery() {
        return sqlQuery;
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
