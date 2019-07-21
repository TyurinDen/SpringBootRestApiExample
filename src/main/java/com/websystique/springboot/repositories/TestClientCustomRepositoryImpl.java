package com.websystique.springboot.repositories;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Qualifier(value = "TestClientCustomRepositoryImpl")
@Repository
public class TestClientCustomRepositoryImpl implements TestClientCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getClientsByCommandFromVkInfoBot(String sqlQuery) {
        Query query = entityManager.createNativeQuery(sqlQuery);
        return (List<Object[]>) query.getResultList();
    }

}
