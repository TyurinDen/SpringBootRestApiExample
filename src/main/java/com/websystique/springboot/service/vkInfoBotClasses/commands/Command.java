package com.websystique.springboot.service.vkInfoBotClasses.commands;

import com.websystique.springboot.service.vkInfoBotClasses.entities.Client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Command {
    private String commandNameRegex; //regex возможных имен команды
    /*
         добавить реджексы для проверки корректности аргументов команды
        добавить интовое поле количество аргументов
        на вход строка из сообщения пользователя ??
        класс должен уметь проверять относится ли к нему переданное имя команды
        если да, то он должен проверить аргументы на корректность, если они некорректны, то
        команда не может быть выполнена, соотвественно она не может быть выполнена
        то выполнять ее нужно здесь же
        или это должен быть класс, который принимает на вход лист команд, находит нужную,
        проверяет аргументы и выполняет эту команду используя запрос из нее и параметризированный
        тип возвращаемого значения.
        ему на вход поступает строка из сообщения пользователя, он ее обрабатывает, определяет содержит ли
        сообщение команду, если да, то уже класс Команда проверяет корректность аргументов и если они корректны,
        то Экзекутор команду выполняет с помощью ЕМ используя SQL запрос из Команды. При этом тип возвращаемого значения
        тоже должен определяться классом Команда. Или таки в классе Команда должен быть метод execute, которому Executor
        передаст ЕМ. Этот метод выполнит запрос и вернет результат нужного типа, который был указан при создании объекта
        класса Команда.
    */
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
        String[] commandComponents = messageText.trim().toLowerCase().split(" ");
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

    public Future<List<Client>> execute(EntityManager entityManager, ExecutorService executorService) {
        queryString = String.format(queryBasisString, args);
        return executorService.submit(() -> entityManager.getResultList(queryBasisString));

//        Query query = entityManager.createNativeQuery(queryString);
//        return query.getResultList();

    }

}
