package pl.training.jpa;

import jakarta.persistence.RollbackException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static pl.training.jpa.TestUtils.*;

class JpaTest {

    private final Client client = Fixtures.testClient();
    private final Payment payment = Fixtures.testPayment(BigDecimal.valueOf(1_000));
    private final Post post = Fixtures.testPost("Test post", "Test content");
    private final Tag tag = Fixtures.testTag("Java");
    private final Tag secondTag = Fixtures.testTag("Kotlin");
    private final Author author = Fixtures.testAuthor();
    private final Comment comment = Fixtures.testComment();
    private Client clientProxy;
    private Set<Tag> tagsProxy;

    @BeforeEach
    void beforeEach() {
        STATISTICS.clear();
        post.setTags(Set.of(tag, secondTag));
        post.setComments(List.of(comment));
        comment.setAuthor(author);
        author.setComments(List.of(comment));
    }

    @AfterAll
    static void afterAll() {
        ENTITY_MANAGER_FACTORY.close();
    }

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
        var newFirstName = "Marek";
        run(entityManager -> {
            entityManager.persist(client);
            // entityManager.flush();
            client.setFirstName(newFirstName);
        });
        run(entityManager -> {
            var persistedClient = entityManager.find(Client.class, client.getId());
            assertEquals(newFirstName, persistedClient.getFirstName());
        });
    }

    @Test
    void given_a_detached_entity_when_merge_then_entity_state_is_automatically_synchronized_with_the_database_and_managed_entity_is_returned() {
        var newFirstName = "Marek";
        run(entityManager -> entityManager.persist(client));
        run(entityManager -> {
            client.setFirstName(newFirstName);
            var managedClient = entityManager.merge(client);
        });
        run(entityManager -> {
            var persistedClient = entityManager.find(Client.class, client.getId());
            assertEquals(newFirstName, persistedClient.getFirstName());
        });
    }

    @Test
    void given_an_attached_entity_when_remove_then_entity_state_is_removed_from_database() {
        run(entityManager -> entityManager.persist(client));
        run(entityManager -> {
            var persistedClient = entityManager.find(Client.class, client.getId());
            entityManager.remove(persistedClient);
        });
        run(entityManager -> assertNull(entityManager.find(Client.class, client.getId())));
    }

    @Test
    void given_a_detached_entity_when_remove_then_throws_an_exception() {
        run(entityManager -> {
            entityManager.persist(client);
            entityManager.detach(client);
            // entityManager.clear();
            assertThrows(IllegalArgumentException.class, () -> entityManager.remove(client));
        });
    }

    @Test
    void given_an_entity_when_refresh_then_entity_state_is_synchronized_with_database_state() {
        var newFirstName = "Marek";
        run(entityManager -> entityManager.persist(client));
        run(entityManager -> {
            var persistedClient = entityManager.find(Client.class, client.getId());
            updateClientAsync(newFirstName);
            sleep(500);
            persistedClient.setFirstName("Test name");
            entityManager.refresh(persistedClient);
            assertEquals(newFirstName, persistedClient.getFirstName());
        });
    }

    private void updateClientAsync(String newFirstName) {
        runAsync(entityManager -> {
            var persistedClient = entityManager.find(Client.class, client.getId());
            persistedClient.setFirstName(newFirstName);
        });
    }

    @Test
    void given_a_persisted_entity_when_get_reference_then_entity_state_is_lazy_loaded() {
        run(entityManager -> entityManager.persist(client));
        run(entityManager -> {
            clientProxy = entityManager.getReference(Client.class, client.getId());
            var clientId = clientProxy.getId();
            assertEquals(0, STATISTICS.getEntityLoadCount());
            var clientFirstName = clientProxy.getFirstName();
            assertEquals(1, STATISTICS.getEntityLoadCount());
        });
    }

    @Test
    void given_a_persisted_entity_when_get_reference_after_transaction_ic_closed_then_throws_an_exception() {
        run(entityManager -> entityManager.persist(client));
        run(entityManager -> clientProxy = entityManager.getReference(Client.class, client.getId()));
        assertThrows(LazyInitializationException.class, () -> clientProxy.getFirstName());
    }

    @Test
    void given_an_entity_with_custom_mappings_when_persist_then_entity_state_is_saved_into_database() {
        // var paymentId = Fixtures.testPaymentId(); // @IdClass, @Embeddable

        /* @IdClass
        payment.setId(paymentId.getId());
        payment.setExternalTransactionId(paymentId.getExternalTransactionId());
        */

        /* @Embeddable
        payment.setId(paymentId);
        */

        payment.setId(Fixtures.uuid());
        payment.setExternalTransactionId(Fixtures.uuid());

        run(entityManager -> entityManager.persist(payment));
        run(entityManager -> {
            // var persistedPayment = entityManager.find(Payment.class, paymentId); // @IdClass, @Embeddable
            var persistedPayment = entityManager.find(Payment.class, payment.getId());
            assertNotNull(persistedPayment);
        });
    }

    private void createPost() {
        run(entityManager -> {
            entityManager.persist(post);
            // entityManager.persist(comment); // CASCADE
            entityManager.persist(author);
            entityManager.persist(tag);
            entityManager.persist(secondTag);
        });
    }

    @Test
    void given_entities_with_relations_when_persist_all_entities_then_entities_state_and_relations_are_synchronized_with_database() {
        createPost();
        run(entityManager -> {
            var persistedPost = entityManager.find(Post.class, post.getId());
            assertNotNull(persistedPost);
        });
    }

    @Test
    void given_entities_with_relations_when_persist_some_entities_then_throws_exception() {
        assertThrows(RollbackException.class, () -> run(entityManager -> entityManager.persist(post)));
    }

    @Test
    void given_entities_with_relations_when_find_then_return_post_without_comments_and_tags() {
        createPost();
        run(entityManager -> {
            var persistedPost = entityManager.find(Post.class, post.getId());
            assertEquals(1, STATISTICS.getEntityLoadCount());
        });
    }

    @Test
    void given_entities_with_relations_when_find_then_lazy_loads_tags() {
        createPost();
        run(entityManager -> {
            var persistedPost = entityManager.find(Post.class, post.getId());
            var tags = persistedPost.getTags();
            assertEquals(1, STATISTICS.getEntityLoadCount());
            tags.forEach(System.out::println);
            assertEquals(3, STATISTICS.getEntityLoadCount());
        });
    }

    @Test
    void given_entities_with_relations_when_access_tags_and_transaction_is_closed_then_throws_an_exception() {
        createPost();
        run(entityManager -> tagsProxy = entityManager.find(Post.class, post.getId()).getTags());
        assertThrows(LazyInitializationException.class, () -> tagsProxy.forEach(System.out::println));
    }

    @Test
    void given_entities_with_relations_when_join_fetch_tags_then_returns_post_and_tags() {
        createPost();
        run(entityManager -> {
            entityManager.createQuery("select p from Post as p join fetch p.tags t", Post.class).getResultList();
            assertEquals(3, STATISTICS.getEntityLoadCount());
        });
    }

    @Test
    void given_entities_with_relations_when_find_with_entity_graph_then_returns_post_with_tags() {
        createPost();
        run(entityManager -> {
            var entityGraph = entityManager.createEntityGraph(Post.class);
            entityGraph.addAttributeNodes("tags");
            // var entityGraph = entityManager.createEntityGraph(Post.WITH_TAGS);
            // All attributes specified in entity graph will be treated as Eager, and all attribute not specified will be treated as Lazy
            // Map<String, Object> properties = Map.of("jakarta.persistence.fetchgraph", entityGraph);
            // All attributes specified in entity graph will be treated as Eager, and all attribute not specified use their default/mapped value
            Map<String, Object> properties = Map.of("jakarta.persistence.loadgraph", entityGraph);
            var persistedPost = entityManager.find(Post.class, post.getId(), properties);
            assertEquals(3, STATISTICS.getEntityLoadCount());
        });
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

}
