package com.websystique.springboot.service.vkInfoBotClasses.commands;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntityManager {
    public <T> List<T> getResultList(String className) {
        TestClient client = new TestClient();
        if (className.equalsIgnoreCase(TestClient.class.getSimpleName())) {
            return Arrays.asList((T) client);
        }

        return Collections.emptyList(); //TODO или бросать исключение, что наверное более правильно
    }
}
