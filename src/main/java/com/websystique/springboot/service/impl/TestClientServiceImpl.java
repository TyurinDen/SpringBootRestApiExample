package com.websystique.springboot.service.impl;

import com.websystique.springboot.repositories.TestClientRepository;
import com.websystique.springboot.service.TestClientService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TestClientServiceImpl implements TestClientService {

    @Autowired
    TestClientRepository testClientRepository;

    @Override
    public List getTestClientByWildcardId(String wildcardId) {
        return null;
    }
}
