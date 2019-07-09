package com.websystique.springboot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.websystique.springboot.service.VkInfoBotService;
import com.websystique.springboot.service.vkInfoBotClasses.messages.CustomNewEventDeserializer;
import com.websystique.springboot.service.vkInfoBotClasses.messages.NewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/vkInfoBot")
public class VkInfoBotController {

    private final VkInfoBotService vkInfoBotService;
    private final ObjectMapper objectMapper;

    @Autowired
    public VkInfoBotController(VkInfoBotService vkInfoBotService, ObjectMapper objectMapper) {
        this.vkInfoBotService = vkInfoBotService;
        this.objectMapper = objectMapper;

        SimpleModule module = new SimpleModule("CustomNewEventDeserializer");
        module.addDeserializer(NewEvent.class, new CustomNewEventDeserializer());
        objectMapper.registerModule(module);
    }

    @RequestMapping(value = "/getClients", method = RequestMethod.POST)
    public ResponseEntity<String> listOfClients(@RequestBody String jsonBody) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonBody);
        if (jsonNode.get("type").asText().equals("confirmation")) {
            return new ResponseEntity<>(vkInfoBotService.getConfirmationToken(), HttpStatus.OK);
        }
        NewEvent newEvent = objectMapper.readValue(jsonBody, NewEvent.class);
        System.out.println(jsonBody);
        System.out.println(newEvent);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

}
