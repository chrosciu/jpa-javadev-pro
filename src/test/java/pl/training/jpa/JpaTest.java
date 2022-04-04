package pl.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JpaTest {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("training");
    private final Client client = Fixtures.testClient();

    @Test
    void given_an_entity_object_when_persist_then_entity_state_is_saved_into_database() {
        run(entityManager -> entityManager.persist(client));
        run(entityManager -> {
            var persistedClient = entityManager.find(Client.class, client.getId());
            assertEquals(client.getFirstName(), persistedClient.getFirstName());
            assertEquals(client.getLastName(), persistedClient.getLastName());
        });
    }

    void run(Consumer<EntityManager> task) {
        var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        var transaction = entityManager.getTransaction();
        transaction.begin();
        task.accept(entityManager);
        transaction.commit();
        entityManager.close();
    }

    @AfterAll
    static void afterAll() {
        ENTITY_MANAGER_FACTORY.close();
    }

}
