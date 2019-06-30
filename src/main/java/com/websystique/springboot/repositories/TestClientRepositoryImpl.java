package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.entities.TestClient;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository
@Transactional
public class TestClientRepositoryImpl implements TestClientRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(TestClient testClient) {
        entityManager.persist(testClient);
    }

    @Override
    public TestClient getById(Long id) {
        return entityManager.find(TestClient.class, id);
    }

    @Override
    public TestClient getByFirstName(String firstName) {
        Query query = entityManager.createNamedQuery("getByFirstName", TestClient.class);
        query.setParameter("firstName", firstName);
        return (TestClient) query.getSingleResult();
    }

    @Override
    public Iterable<TestClient> getAll() {
        return entityManager.createNamedQuery("getAll", TestClient.class).getResultList();
    }

    @Override
    public Long count() {
        return count();
    }

    @Override
    public void delete(TestClient entity) {
        delete(entity);
    }

    @Override
    public boolean isExists(Long id) {
        return isExists(id);
    }
}
