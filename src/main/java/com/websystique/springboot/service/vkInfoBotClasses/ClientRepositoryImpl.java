package com.websystique.springboot.service.vkInfoBotClasses;

import com.websystique.springboot.service.vkInfoBotClasses.entities.Client;

import java.util.List;

public class ClientRepositoryImpl implements ClientRepository {
    @Override
    public List<Client> findByName(String name) {
        return null;
    }

    @Override
    public Client findById(long id) {
        return null;
    }

    @Override
    public Client findByEmail(String email) {
        return null;
    }

    @Override
    public List<Client> findByAge(int age) {
        return null;
    }
}
