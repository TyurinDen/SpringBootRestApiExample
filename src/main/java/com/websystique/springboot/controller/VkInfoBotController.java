package com.websystique.springboot.controller;

import com.websystique.springboot.service.VkInfoBotService;
import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vkInfoBot")
public class VkInfoBotController {

    private final VkInfoBotService vkInfoBotService;

    @Autowired
    public VkInfoBotController(VkInfoBotService vkInfoBotService) {
        this.vkInfoBotService = vkInfoBotService;
    }

    @RequestMapping(value = "/getClients", method = RequestMethod.GET)
    public ResponseEntity<String> listOfClients(@RequestBody Message message) { // TODO: 08.07.2019 не message, а UpdatesArray
        //vkInfoBotService.findClients(message);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @RequestMapping(value = "/getClients", method = RequestMethod.POST)
    public ResponseEntity<String> confirmation() {
        return new ResponseEntity<>(vkInfoBotService.getConfirmationToken(), HttpStatus.OK);
    }

}
