package com.websystique.springboot.service.vkInfoBotClasses.commands;

import com.websystique.springboot.service.vkInfoBotClasses.entities.Client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Command implements Executable {
    private String commandNameRegex; //regex возможных имен команды
    private int numberOfArgs;
    private String[] argsRegex;
    private String queryBasisString;
    private String queryString;

    public Command(String commandNameRegex, int numberOfArgs) {
        this.commandNameRegex = commandNameRegex;
        this.numberOfArgs = numberOfArgs;
    }

    @Override
    public boolean checkCommand(String messageText) { // TODO: 19.06.2019 method name???
        String[] commandComponents = messageText.split(" ");
        if (commandComponents.length < numberOfArgs + 1) {
            return false;
        }
        if (!commandComponents[0].matches(commandNameRegex)) {
            return false;
        }

        String[] args = Arrays.copyOfRange(commandComponents, 1, numberOfArgs + 1);
        for (int i = 0; i < args.length; i++) {
            if (!args[i].matches(argsRegex[i])) {
                return false;
            }
        }

        return true;

    }

    /* Создаем фабрику команд. Которая по имени команды будет возвращать нужную команду. Каждая команда должна реализовывать
     * общий для команд интерфейс. CommandExecutor будет передевать фабрике имя команды, фабрика возвращать объект команды.
     * Далее уже сама команда будет проверять аргументы. СЕ будет таким образом отправлять два разных сообщения - нет команды,
     * аргументы некорректны.
     * Таким образом появляется возможность реализовывать команду как угодно, главное реализовать методы интерфейса.
     * В конструкторе команды останется только два параметра, реджекс имени и количество аргументов.
     *
     */
    @Override
    public Future<List<Client>> execute(EntityManager entityManager, ExecutorService executorService) {
        //queryString = String.format(queryBasisString, args);
        System.out.println(queryString);
        return executorService.submit(() -> entityManager.getResultList(queryBasisString));

//        Query query = entityManager.createNativeQuery(queryString);
//        return query.getResultList();

    }

    @Override
    public String toString() {
        return "Command{" +
                "commandNameRegex='" + commandNameRegex + '\'' +
                ", numberOfArgs=" + numberOfArgs +
                ", argsRegex=" + Arrays.toString(argsRegex) +
                ", queryBasisString='" + queryBasisString + '\'' +
                ", queryString='" + queryString + '\'' +
                '}';
    }

}
