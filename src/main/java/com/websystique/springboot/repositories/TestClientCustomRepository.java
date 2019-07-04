package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;

public interface TestClientCustomRepository {

    Iterable<TestClient> getByCommandFromVkBot(String command);

}
