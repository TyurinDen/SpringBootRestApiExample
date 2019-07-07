package com.websystique.springboot;

import com.websystique.springboot.controller.VkInfoBotController;
import com.websystique.springboot.service.VkInfoBotService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(VkInfoBotController.class)
public class VkInfoBotControllerTest {
    // TODO: 07.07.2019 переписать, если возможно, так как это не тест, а херня полная

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VkInfoBotService botService;

    @Test
    public void wenPostVkInfoBot_thenReturnConfirmationToken() throws Exception {
        //given(botService.getConfirmationToken()).willReturn(botService.getConfirmationToken());

        mvc.perform(post("/vkInfoBot/getClients")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
    }
}
