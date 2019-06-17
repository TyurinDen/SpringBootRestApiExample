package com.websystique.springboot.service.vkInfoBotClasses.commands;

import java.util.List;

public interface Executable<T> {
    boolean validate(String commandName);
    List<T> execute(EntityManager entityManager);
}
