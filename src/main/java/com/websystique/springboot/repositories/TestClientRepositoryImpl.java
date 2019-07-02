package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class TestClientRepositoryImpl implements TestClientRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Iterable<TestClient> getByIdWithWildcard(String wildcardId) {
        return null;
    }

//    @Override
//    public List<TestClient> getByFirstName(String firstName) {
//        Query query = entityManager.createNamedQuery("getByFirstName", TestClient.class);
//        query.setParameter("firstName", firstName);
//        return query.getResultList();
//    }
//
//    @Override
//    public List<TestClient> getClientByFirstNameStartingWith(String firstName) {
//        return null;
//    }
//
//    @Override
//    public List<TestClient> getByIdWithWildcard(String idWildcard) {
//        return null;
//    }
}
