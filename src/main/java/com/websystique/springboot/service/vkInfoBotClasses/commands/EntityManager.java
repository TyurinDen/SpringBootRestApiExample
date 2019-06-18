package com.websystique.springboot.service.vkInfoBotClasses.commands;

import com.websystique.springboot.service.vkInfoBotClasses.entities.Client;
import com.websystique.springboot.service.vkInfoBotClasses.entities.Student;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntityManager {
    public <T> List<T> getResultList(String className) {
        Client client = new Client(1, "Den", "Tyurin", 38, "ttt@mail.ru");
        Student student = new Student(client, "JAVA!!!!!!");
        if (className.equalsIgnoreCase(Client.class.getSimpleName())) {
            return Arrays.asList((T) client);
        }

        if (className.equalsIgnoreCase(Student.class.getSimpleName())) {
            return Arrays.asList((T) student);
        }
        return Collections.emptyList(); //TODO или бросать исключение, что наверное более правильно
    }
}
