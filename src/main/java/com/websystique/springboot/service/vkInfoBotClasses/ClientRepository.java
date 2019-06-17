package com.websystique.springboot.service.vkInfoBotClasses;

import com.websystique.springboot.service.vkInfoBotClasses.entities.Client;

import java.util.List;

public interface ClientRepository {
    List<Client> findByName(String name);
    Client findById(long id);
    Client findByEmail(String email);
    List<Client> findByAge(int age);
}
