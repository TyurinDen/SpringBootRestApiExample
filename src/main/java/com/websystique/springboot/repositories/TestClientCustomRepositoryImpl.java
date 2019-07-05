package com.websystique.springboot.repositories;

import com.websystique.springboot.service.vkInfoBotClasses.commands.Command;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Qualifier(value = "TestClientCustomRepositoryImpl")
@Repository
public class TestClientCustomRepositoryImpl implements TestClientCustomRepository {
    private Set<Command> commandSet = new HashSet<>();

    @PersistenceContext
    private EntityManager entityManager;

    public TestClientCustomRepositoryImpl() {
        String SQL_QUERY = "SELECT \n" +
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
                "    u.last_name AS Owner_lastname,\n" +
                "    u.first_name AS Owner_name,\n" +
                "    u1.last_name AS Mentor_lastname,\n" +
                "    u1.first_name AS Mentor_name\n" +
                "FROM\n" +
                "    CLIENT cl\n" +
                "        LEFT JOIN\n" +
                "    user u ON cl.owner_user_id = u.user_id\n" +
                "        LEFT JOIN\n" +
                "    user u1 ON cl.owner_mentor_id = u1.user_id\n" +
                "WHERE ";

        Command findById = new Command("^i$|^и$|^ид$|^id$", 1,
                new String[]{"^[0-9]+\\*?$|^\\*[0-9]+$"}, SQL_QUERY + "CLIENT_ID RLIKE('%s')");
        Command findByCityAndLastname = new Command("^cl$|^cln$|^сл$|^слн$", 2,
                new String[]{"^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$", "^[a-zа-я]+\\*?$|^\\*[a-zа-я]+$"},
                SQL_QUERY + "CITY RLIKE('%s') AND LAST_NAME RLIKE('%s')");

    }

    @Override
    public Iterable<List<Object[]>> getByCommandFromVkBot(String messageText, Command command) {
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
