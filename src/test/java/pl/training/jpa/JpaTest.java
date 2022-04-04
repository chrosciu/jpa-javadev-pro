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

    @Test
    void given_an_attached_entity_when_entity_state_is_changed_then_entity_state_is_automatically_synchronized_with_the_database() {
    }

    @Test
    void given_a_detached_entity_when_merge_then_entity_state_is_automatically_synchronized_with_the_database_and_managed_entity_is_returned() {
    }

    @Test
    void given_an_attached_entity_when_remove_then_entity_state_is_removed_from_database() {
    }

    @Test
    void given_a_detached_entity_when_remove_then_throws_an_exception() {
    }

    @Test
    void given_an_entity_when_refresh_then_entity_state_is_synchronized_with_database_state() {
    }

    @Test
    void given_a_persisted_entity_when_get_reference_then_entity_state_is_lazy_loaded() {
    }

    @Test
    void given_a_persisted_entity_when_get_reference_after_transaction_ic_closed_then_throws_an_exception() {
    }

    @Test
    void given_an_entity_with_custom_mappings_when_persist_then_entity_state_is_saved_into_database() {
    }

    @Test
    void given_entities_with_relations_when_persist_all_entities_then_entities_state_and_relations_are_synchronized_with_database() {
    }

    @Test
    void given_entities_with_relations_when_persist_some_entities_then_throws_exception() {
    }

    @Test
    void given_entities_with_relations_when_find_then_return_post_without_comments_and_tags() {
    }

    @Test
    void given_entities_with_relations_when_find_then_lazy_loads_tags() {
    }

    @Test
    void given_entities_with_relations_when_access_tags_and_transaction_is_closed_then_throws_an_exception() {
    }

    @Test
    void given_entities_with_relations_when_join_fetch_tags_then_returns_post_and_tags() {
    }

    @Test
    void given_entities_with_relations_when_find_with_entity_graph_then_returns_post_with_tags() {
    }

    @Test
    void given_entity_when_query_with_constructor_expression_then_returns_entity_projection() {
    }

    @Test
    void given_many_entities_when_bulk_update_then_updates_the_database_without_loading_entities() {
    }

    @Test
    void given_many_entities_when_clear_then_releases_managed_entities() {
    }

    @Test
    void given_versioned_entity_when_first_transaction_tires_to_override_changes_from_second_transaction_then_first_transaction_is_rolled_back() throws InterruptedException {
    }

    @Test
    void given_two_transactions_when_first_transactions_acquired_the_lock_then_second_transaction_waits_for_first_transaction_to_release_the_lock() throws InterruptedException {
    }

    @AfterAll
    static void afterAll() {
        ENTITY_MANAGER_FACTORY.close();
    }

}
