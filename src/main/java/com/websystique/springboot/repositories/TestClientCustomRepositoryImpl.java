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
    final String mainPartOfSqlQuery = "SELECT \n" +
            "    cl.client_id,\n" +
            "    cl.last_name,\n" +
            "    cl.first_name,\n" +
            "    cl.email,\n" +
            "    cl.phone_number,\n" +
            "    cl.city,\n" +
            "    cl.country,\n" +
            "    cl.client_description_comment,\n" +
            "    cl.comment,\n" +
            "    cl.client_state,\n" +
            "    cl.postpone_comment,\n" +
            "    cl.birth_date,\n" +
            "    u.last_name AS Owner_User_lastname,\n" +
            "    u.first_name AS Owner_User_name,\n" +
            "    u1.last_name AS Owner_Mentor_lastname,\n" +
            "    u1.first_name AS Owner_Mentor_name\n" +
            "FROM\n" +
            "    CLIENT cl\n" +
            "        LEFT JOIN\n" +
            "    user u ON cl.owner_user_id = u.user_id\n" +
            "        LEFT JOIN\n" +
            "    user u1 ON cl.owner_mentor_id = u1.user_id\n" +
            "WHERE ";


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Iterable<TestClient> getByCommandFromVkBot(String command) {

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
//    public List<TestClient> getByWildcardId(String idWildcard) {
//        return null;
//    }
}
