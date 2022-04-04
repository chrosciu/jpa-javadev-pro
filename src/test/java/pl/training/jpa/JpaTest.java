package pl.training.jpa;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.training.jpa.TestUtils.ENTITY_MANAGER_FACTORY;
import static pl.training.jpa.TestUtils.run;

class JpaTest {

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

    @AfterAll
    static void afterAll() {
        ENTITY_MANAGER_FACTORY.close();
    }

}
