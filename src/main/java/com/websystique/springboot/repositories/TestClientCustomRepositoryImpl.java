package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
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
    public Iterable<TestClient> getByWildcard(String wildcard) {
        Query query = entityManager.createNativeQuery("SELECT * FROM test_clients WHERE first_name like '%a'",
                TestClient.class);
        //query.setParameter("wildcard", wildcard);
        return (List<TestClient>) query.getResultList();
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
//    public List<TestClient> getByWildcardId(String idWildcard) {
//        return null;
//    }
}
