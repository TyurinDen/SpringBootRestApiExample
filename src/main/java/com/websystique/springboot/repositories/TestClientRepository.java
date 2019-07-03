package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;

public interface TestClientRepository extends GenericRepository<TestClient, Long>, TestClientCustomRepository {

    Iterable<TestClient> getByFirstName(String firstName);

    Iterable<TestClient> getClientByFirstNameStartingWith(String firstName);

}
