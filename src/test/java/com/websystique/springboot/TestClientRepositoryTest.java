package com.websystique.springboot;

import com.websystique.springboot.repositories.TestClientRepository;
import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestClientRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    TestClientRepository testClientRepository;

    @Test
    public void whenFindByName_thenReturnListOfTestClients() {
        TestClient alex = TestClient.builder().firstName("Alex").build();
        entityManager.persist(alex);
        entityManager.flush();

        List<TestClient> found = (List<TestClient>) testClientRepository.getByFirstName("Alex");
        assertThat(found.size()).isEqualTo(1);
    }

    @Test
    public void whenFindByIdStartsWith_thenReturnListOfTestClients() {
        TestClient alex = TestClient.builder().firstName("Alex").build();
        TestClient alba = TestClient.builder().firstName("Alba").build();
        TestClient bill = TestClient.builder().firstName("Bill").build();
        entityManager.persist(alex);
        entityManager.persist(alba);
        entityManager.persist(bill);
        entityManager.flush();

//        List<TestClient> found = (List<TestClient>) testClientRepository.getClientByIdStartsWith(1L);
//        assertThat(found.size()).isEqualTo(2);
        List<TestClient> found = (List<TestClient>) testClientRepository.getClientByFirstNameStartingWith("A");
        assertThat(found.size()).isEqualTo(2);
    }
}
