package com.websystique.springboot;

import com.websystique.springboot.repositories.TestClientRepository;
import com.websystique.springboot.repositories.TestClientCustomRepository;
import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Qualifier("TestClientCustomRepositoryImpl")
    @Autowired
    TestClientCustomRepository clientCustomRepository;

    @Test
    public void whenGetByName_thenReturnListOfTestClients() {
        List<TestClient> found = (List<TestClient>) testClientRepository.getByFirstName("Alex");
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    public void whenGetByFirstNameStartsWith_thenReturnListOfTestClients() {
        List<TestClient> found = (List<TestClient>) testClientRepository.getClientByFirstNameStartingWith("J");
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    public void whenGetByWildcard_thenReturnListOfTestClients() {
        //List<TestClient> found = (List<TestClient>) clientCustomRepository.getByWildcard("cl.first_name like(\"%a\")");
        List<TestClient> found = (List<TestClient>) clientCustomRepository.getByWildcard("");
        assertThat(found.size()).isEqualTo(2);
    }

    @Before
    public void populateDummyUsers() {
        TestClient jamesVax = TestClient.builder().firstName("James").lastName("Vax").build();
        TestClient johnSmith = TestClient.builder().firstName("John").lastName("Smith").build();
        TestClient billBonce = TestClient.builder().firstName("Bill").lastName("Bonce").build();
        TestClient alexSmith = TestClient.builder().firstName("Alex").lastName("Smith").build();
        TestClient alexConnor = TestClient.builder().firstName("Alex").lastName("Connor").build();
        TestClient billBucket = TestClient.builder().firstName("Bill").lastName("Bucket").build();
        entityManager.persist(jamesVax);
        entityManager.persist(johnSmith);
        entityManager.persist(billBonce);
        entityManager.persist(alexSmith);
        entityManager.persist(alexConnor);
        entityManager.persist(billBucket);
        entityManager.flush();
    }

}
