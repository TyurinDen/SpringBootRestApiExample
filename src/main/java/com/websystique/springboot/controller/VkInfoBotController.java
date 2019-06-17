package com.websystique.springboot.controller;

import com.websystique.springboot.service.impl.VkInfoBotServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/getLongPollServer")
public class VkInfoBotController {

    @Autowired
    VkInfoBotServiceImpl botJmCrmService;

    @GetMapping
    public String serverAnswerForCallbackApi() throws IOException {
        return "123"; //botJmCrmService.getLongPollServer(172488725);
    }

//    @GetMapping
//    public String serverAnswerForCallbackApi() {
//        return "3dffcc47";
//    }
}
