package com.websystique.springboot;

import com.websystique.springboot.repositories.TestClientRepository;
import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestClientRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    TestClientRepository testClientRepository;

    @Test
    public void whenFindByName_thenReturnTestClient() {
        TestClient alex = new TestClient();
        alex.setFirstName("Alex");
        entityManager.persist(alex);
        entityManager.flush();

        TestClient found = testClientRepository.getByFirstName("Alex");
        assertThat(found.getFirstName()).isEqualTo(alex.getFirstName());
    }
}
