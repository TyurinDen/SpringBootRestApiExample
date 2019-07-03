package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;

public interface TestClientCustomRepository {

    Iterable<TestClient> getByWildcard(String wildcard);

}
