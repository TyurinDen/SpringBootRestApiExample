package com.websystique.springboot;

import com.websystique.springboot.repositories.TestClientRepository;
import com.websystique.springboot.service.TestClientService;
import com.websystique.springboot.service.impl.TestClientServiceImpl;
import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
public class TestClientServiceTest {
    // TODO: 07.07.2019 переписать тест когда будет готов VkInfoBotService

    @TestConfiguration
    static class TestClientServiceImplTestContextConfiguration {
        @Bean
        public TestClientService testClientService() {
            return new TestClientServiceImpl();
        }
    }

    @Autowired
    private TestClientService testClientService;

    @MockBean
    private TestClientRepository testClientRepository;

    @Before
    public void setUp() {
        TestClient jamesVax = TestClient.builder().firstName("James").lastName("Vax").build();
        List<TestClient> testClientList = new ArrayList<>(Arrays.asList(jamesVax));
        Mockito.when(testClientRepository.getByFirstName(jamesVax.getFirstName())).thenReturn(testClientList);
    }
}
