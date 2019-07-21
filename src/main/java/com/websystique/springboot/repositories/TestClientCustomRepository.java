package com.websystique.springboot.repositories;

import java.util.List;

public interface TestClientCustomRepository {

    List<Object[]> getClientsByCommandFromVkInfoBot(String sqlQuery);

}
