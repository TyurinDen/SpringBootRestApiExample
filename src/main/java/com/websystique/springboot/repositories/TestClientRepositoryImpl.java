package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class TestClientRepositoryImpl extends SimpleJpaRepository<TestClient, Long> implements TestClientRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public TestClientRepositoryImpl(JpaEntityInformation<TestClient, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        Example
    }

    public TestClientRepositoryImpl(Class<TestClient> domainClass, EntityManager em) {
        super(domainClass, em);
    }


    @Override
    public List<TestClient> getByFirstName(String firstName) {
        Query query = entityManager.createNamedQuery("getByFirstName", TestClient.class);
        query.setParameter("firstName", firstName);
        return query.getResultList();
    }

    @Override
    public List<TestClient> getClientByFirstNameStartingWith(String firstName) {
        return null;
    }

    @Override
    public List<TestClient> getByIdWithWildcard(String idWildcard) {
        return null;
    }
}
