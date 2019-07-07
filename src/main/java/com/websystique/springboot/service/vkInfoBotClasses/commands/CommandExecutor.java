package com.websystique.springboot.service.vkInfoBotClasses.commands;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public final class CommandExecutor {
    private final ExecutorService executorService;
    private final List<Command> commandList;
    private final EntityManager entityManager;

    // TODO: 08.06.2019 надо ли делать этот класс синглтоном?
    public CommandExecutor(List<Command> commandList, EntityManager entityManager, ExecutorService executorService) {
        this.commandList = commandList;
        this.entityManager = entityManager;
        this.executorService = executorService;
    }

    public void executeCommandsList(Queue<Message> inMessages, Map<Message, Future<List<TestClient>>> mapOfRunningCommands,
                                    Queue<Message> outMessages) {
        Message message;
        while ((message = inMessages.poll()) != null) {
            Command command = getCommand(message.getText().trim().toLowerCase());
            if (command != null) {
                //mapOfRunningCommands.put(message, command.execute(entityManager, executorService));
            } else {
                message.setText("ERROR: The command is incorrect!"); // TODO: 10.06.2019 справочное сообщение отправляться должно
                outMessages.offer(message);
            }
        }
    }

    private Command getCommand(String messageText) {
        for (Command command : commandList) {
            if (command.checkSyntax(messageText)) {
                return command;
            }
        }
        return null;
    }

}
