package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.springframework.stereotype.Repository;

@Repository
public interface TestClientRepository extends GenericRepository<TestClient, Long>, TestClientRepositoryCustom {

    Iterable<TestClient> getByFirstName(String firstName);

    Iterable<TestClient> getClientByFirstNameStartingWith(String firstName);

}
