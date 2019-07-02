package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;

public interface TestClientRepositoryCustom {

    Iterable<TestClient> getByIdWithWildcard(String wildcardId);

}
