package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.springframework.data.repository.Repository;


public interface TestClientRepository extends Repository<TestClient, Long> {

    void save(TestClient entity);

    TestClient getById(Long id);

    TestClient getByFirstName(String firstName);

    Iterable<TestClient> getAll();

    Long count();

    void delete(TestClient entity);

    boolean isExists(Long id);

}
