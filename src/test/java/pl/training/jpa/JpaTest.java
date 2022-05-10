package pl.training.jpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.RollbackException;
import net.sf.ehcache.CacheManager;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static pl.training.jpa.TestUtils.*;

class JpaTest {

    private static final int RECORDS_COUNT = 10_000;

    private final Client client = Fixtures.testClient();
    private final Payment payment = Fixtures.testPayment(BigDecimal.valueOf(1_000));
    private final Post post = Fixtures.testPost("Test title", "Test content");
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
            assertEquals(client.getLastName(), persistedClient.getLastName());
            assertEquals(client.getFirstName(), persistedClient.getFirstName());
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
            updateAsyncClient(newFirstName);
            sleep(500);
            persistedClient.setFirstName("Test");
            entityManager.refresh(persistedClient);
            assertEquals(newFirstName, persistedClient.getFirstName());
        });
    }

    private void updateAsyncClient(String newFirstName) {
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
            var clientId= clientProxy.getId();
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
           // entityManager.persist(comment);
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
            entityManager.createQuery("select p from Post p join fetch p.tags", Post.class).getResultList();
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
        createPost();
        run(entityManager -> {
            var postInfo = entityManager.createQuery("select new pl.training.jpa.PostInfo(p.id, p.title) from Post p", PostInfo.class)
                    .getSingleResult();
            assertEquals(post.getTitle(), postInfo.getTitle());
        });
    }

    private void createPayments() {
        run(entityManager -> {
            for (int record = 1; record <= RECORDS_COUNT; record++) {
                var payment = Fixtures.testPayment(BigDecimal.valueOf(10_000));
                payment.setId(Fixtures.uuid());
                payment.setExternalTransactionId(Fixtures.uuid());
                entityManager.persist(payment);
            }
        });
    }

    @Test
    void given_many_entities_when_bulk_update_then_updates_the_database_without_loading_entities() {
       createPayments();
       System.out.println("##################################################################");
       measure(() -> run(entityManager -> entityManager.createQuery("update Payment p set p.money.value = :value")
               .setParameter("value", 500)
               .executeUpdate())); // ~200 ms
    }

    @Test
    void given_many_entities_when_clear_then_releases_managed_entities() {
        createPayments();
        System.out.println("##################################################################");
        measure(() -> run(entityManager -> {
            var pageSize = 100;
            var pagesCount = RECORDS_COUNT / pageSize;
            for(int page = 0; page <= pagesCount; page++) {
                entityManager.createQuery("select p from Payment p", Payment.class)
                        .setFirstResult(page * pageSize)
                        .setMaxResults(pageSize)
                        .getResultList()
                        .forEach(record -> record.updateValue(BigDecimal.valueOf(500)));
                entityManager.clear();
            }
        })); // ~53 000 ms dla pageSize = RECORDS_COUNT, ~50 000 ms dla pageSize = 1_000, ~53 000 ms dla pageSize = 100

       /* measure(() -> {
            var pageSize = 100;
            var pagesCount = RECORDS_COUNT / pageSize;
            for(int page = 0; page <= pagesCount; page++) {
                var pageNumber = page * pageSize;
                run(entityManager -> {
                    entityManager.createQuery("select p from Payment p", Payment.class)
                            .setFirstResult(pageNumber)
                            .setMaxResults(pageSize)
                            .getResultList()
                            .forEach(record -> record.updateValue(BigDecimal.valueOf(500)));
                    entityManager.clear();
                });
            }
        }); // ~52 000 ms dla pageSize = 100*/
    }

    @Test
    void given_versioned_entity_when_first_transaction_tires_to_override_changes_from_second_transaction_then_first_transaction_is_rolled_back() throws InterruptedException {
        payment.setId(Fixtures.uuid());
        payment.setExternalTransactionId(Fixtures.uuid());
        run(entityManager -> entityManager.persist(payment));
        execute(List.of(
                new UpdatePaymentTask(payment.getId(), BigDecimal.valueOf(3_000), LockModeType.NONE, 1, 5),
                new UpdatePaymentTask(payment.getId(), BigDecimal.valueOf(6_000), LockModeType.NONE, 2, 3)
        ));
        run(entityManager -> {
            var persistedPayment = entityManager.find(Payment.class, payment.getId());
            assertEquals(BigDecimal.valueOf(6_000).setScale(2), persistedPayment.getMoney().getValue());
        });
    }

    @Test
    void given_two_transactions_when_first_transactions_acquired_the_lock_then_second_transaction_waits_for_first_transaction_to_release_the_lock() throws InterruptedException {
        payment.setId(Fixtures.uuid());
        payment.setExternalTransactionId(Fixtures.uuid());
        run(entityManager -> entityManager.persist(payment));
        execute(List.of(
                new UpdatePaymentTask(payment.getId(), BigDecimal.valueOf(3_000), LockModeType.PESSIMISTIC_WRITE, 1, 5),
                new UpdatePaymentTask(payment.getId(), BigDecimal.valueOf(6_000), LockModeType.PESSIMISTIC_WRITE, 2, 3)
        ));
        run(entityManager -> {
            var persistedPayment = entityManager.find(Payment.class, payment.getId());
            assertEquals(BigDecimal.valueOf(6_000).setScale(2), persistedPayment.getMoney().getValue());
        });
    }

    @Test
    void given_entities_with_inheritance_when_persist_all_entities_then_entities_state_and_relations_are_synchronized_with_database() {
        var employee = new Employee();
        employee.setName("Jan Kowalski");
        var contractEmployee = new ContractEmployee();
        contractEmployee.setName("Marek Nowak");
        contractEmployee.setContractType("b2b");
        run(entityManager -> {
            entityManager.persist(employee);
            entityManager.persist(contractEmployee);
        });
    }

    @Test
    void given_custom_id_generator_when_persist_then_id_is_assigned_to_entity() {
        run(entityManager -> {
            var account = new Account();
            account.setNumber("00000000000000000000000001");
            account.setValue(1000);
            entityManager.persist(account);
        });
    }

    private void createTrainings() {
        run(entityManager -> {
            var javaTag = new Tag();
            javaTag.setId(Fixtures.uuid());
            javaTag.setName("java");

            var oopTag = new Tag();
            oopTag.setId(Fixtures.uuid());
            oopTag.setName("oop");

            var firstAuthor = new Person();
            firstAuthor.setId(Fixtures.uuid());
            firstAuthor.setFirstName("Jan");
            firstAuthor.setLastName("Kowalski");
            firstAuthor.setEmails(Set.of("jan.kowalski@trainig.pl"));

            var secondAuthor = new Person();
            secondAuthor.setId(Fixtures.uuid());
            secondAuthor.setFirstName("Marek");
            secondAuthor.setLastName("Nowak");
            secondAuthor.setEmails(Set.of("marek.nowak@trainig.pl", "mnowak@gmail.com"));

            var firstTrainingDuration = new Duration();
            firstTrainingDuration.setValue(40L);
            firstTrainingDuration.setUnit(DurationUnit.HOURS);

            var firstTraining = new Training();
            firstTraining.setId(Fixtures.uuid());
            firstTraining.setAuthors(List.of(firstAuthor));
            firstTraining.setCode("JPR");
            firstTraining.setDifficulty(Difficulty.BASIC);
            firstTraining.setDescription("Programming in Java");
            firstTraining.setTags(Set.of(oopTag, javaTag));
            firstTraining.setTitle("Programming in Java");
            firstTraining.setType(TrainingType.STATIONARY);
            firstTraining.setDuration(firstTrainingDuration);

            var secondTrainingDuration = new Duration();
            secondTrainingDuration.setValue(13L);
            secondTrainingDuration.setUnit(DurationUnit.HOURS);

            var secondTraining = new Training();
            secondTraining.setId(Fixtures.uuid());
            secondTraining.setAuthors(List.of(firstAuthor, secondAuthor));
            secondTraining.setCode("CPR");
            secondTraining.setDifficulty(Difficulty.BASIC);
            secondTraining.setDescription("Programming in C++");
            secondTraining.setTags(Set.of(oopTag));
            secondTraining.setTitle("Programming in C++");
            secondTraining.setType(TrainingType.STATIONARY);
            secondTraining.setDuration(secondTrainingDuration);

            entityManager.persist(javaTag);
            entityManager.persist(oopTag);
            entityManager.persist(firstAuthor);
            entityManager.persist(secondAuthor);
            entityManager.persist(firstTraining);
            entityManager.persist(secondTraining);
        });
        System.out.println("#######################################################################################################");
    }

    @Test
    void select_all_trainings() {
        createTrainings();
        run(entityManager -> {
            var pageNumber = 0;
            var pageSize = 10;

            // var query = entityManager.createNamedQuery(Training.ALL, Training.class);

            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Training.class);
            var trainingsRoot = criteriaQuery.from(Training.class);

            var query = entityManager.createQuery(criteriaQuery);

            var result  = query.setHint("jakarta.persistence.fetchgraph", entityManager.createEntityGraph(Training.WITH_ALL))
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
            result.forEach(System.out::println);
        });
    }

    @Test
    void select_trainings_by_title() {
        createTrainings();
        run(entityManager -> {
            /*var query = entityManager.createQuery("select t from Training t where t.title = :title", Training.class)
                    .setParameter("title", "Programming in C++");*/

            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Training.class);
            var trainingsRoot = criteriaQuery.from(Training.class);
            criteriaQuery.select(trainingsRoot)
                    .where(criteriaBuilder.equal(trainingsRoot.<String>get("title"), "Programming in C++"));
            var query = entityManager.createQuery(criteriaQuery);

            var result  = query.getSingleResult();
            System.out.println(result);
        });
    }

    @Test
    void select_trainings_code_and_title() {
        createTrainings();
        run(entityManager -> {
            var pageNumber = 0;
            var pageSize = 10;

            // var query = entityManager.createQuery("select t.code, t.title from Training t", Object[].class);

            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createTupleQuery();
            var trainingsRoot = criteriaQuery.from(Training.class);
            criteriaQuery.multiselect(trainingsRoot.<String>get("code").alias("code"), trainingsRoot.<String>get("title").alias("title"));
            var query = entityManager.createQuery(criteriaQuery);

            var result  = query.setHint("jakarta.persistence.fetchgraph", entityManager.createEntityGraph(Training.WITH_ALL))
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

            // result.forEach(record -> System.out.println(Arrays.toString(record)));

            result.forEach(record -> System.out.println(record.get("code") + " " + record.get("title")));
        });
    }

    @Test
    void select_trainings_projection() {
        createTrainings();
        run(entityManager -> {
            var pageNumber = 0;
            var pageSize = 10;

            // var query = entityManager.createQuery("select new pl.training.jpa.TrainingView(t.code, t.title) from Training t", TrainingView.class);

            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(TrainingView.class);
            var trainingsRoot = criteriaQuery.from(Training.class);
            criteriaQuery.select(criteriaBuilder.construct(TrainingView.class, trainingsRoot.<String>get("code").alias("code"), trainingsRoot.<String>get("title").alias("title")));
            var query = entityManager.createQuery(criteriaQuery);

            var result  = query.setHint("jakarta.persistence.fetchgraph", entityManager.createEntityGraph(Training.WITH_ALL))
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

            result.forEach(System.out::println);
        });
    }

    @Test
    void select_trainings_authors_last_name_and_trainings_title() {
        createTrainings();
        run(entityManager -> {
            var pageNumber = 0;
            var pageSize = 10;

            // var query = entityManager.createQuery("select a.lastName, t.title from Training t join t.authors a", Object[].class);

            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Object[].class);
            var trainingsRoot = criteriaQuery.from(Training.class);
            var authorsJoin = trainingsRoot.<Training, Person>join("authors");
            criteriaQuery.multiselect(authorsJoin.<String>get("lastName"), trainingsRoot.<String>get("title"));
            var query = entityManager.createQuery(criteriaQuery);

            var result  = query.setHint("jakarta.persistence.fetchgraph", entityManager.createEntityGraph(Training.WITH_ALL))
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

            result.forEach(record -> System.out.println(Arrays.toString(record)));
        });
    }

    @Test
    void select_trainings_by_tags() {
        createTrainings();
        run(entityManager -> {
            var pageNumber = 0;
            var pageSize = 10;
            var tags = List.of("java", "oop");

            /* var query = entityManager.createQuery("select t from Training t join t.tags ta where ta.name in :tags group by t having count (t) = :count order by t.code", Training.class)
                .setParameter("tags", tags)
                .setParameter("count", tags.size());*/

            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Training.class);
            var trainingsRoot = criteriaQuery.from(Training.class);
            var tagsJoin = trainingsRoot.<Training, Tag>join("tags");
            criteriaQuery.select(trainingsRoot)
                    .where(tagsJoin.get("name").in(tags))
                    .groupBy(trainingsRoot)
                    .having(criteriaBuilder.equal(criteriaBuilder.count(trainingsRoot), tags.size()))
                    .orderBy(criteriaBuilder.asc(trainingsRoot.get("code")));
            var query = entityManager.createQuery(criteriaQuery);

            var result  = query
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

            result.forEach(System.out::println);
        });
    }

    @Test
    void select_trainings_with_duration() {
        createTrainings();
        run(entityManager -> {
            var pageNumber = 0;
            var pageSize = 10;

            /*var query = entityManager.createQuery("select t from Training t where t.duration.unit = :unitType and duration.value between :minHours and :maxHours", Training.class)
                    .setParameter("unitType", DurationUnit.HOURS)
                    .setParameter("minHours", 10L)
                    .setParameter("maxHours", 15L);*/

            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Training.class);
            var trainingsRoot = criteriaQuery.from(Training.class);
            criteriaQuery.select(trainingsRoot)
                    .where(criteriaBuilder.and(
                            criteriaBuilder.equal(trainingsRoot.get("duration").get("unit"), DurationUnit.HOURS),
                            criteriaBuilder.between(trainingsRoot.get("duration").get("value"), 10L, 15L)
                    ));
            var query = entityManager.createQuery(criteriaQuery);

            var result  = query
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

            result.forEach(System.out::println);
        });
    }

    @Test
    void select_trainings_authors_last_name_and_trainings_count_when_trainings_count_is_greater_or_equal_two() {
        createTrainings();
        run(entityManager -> {
            var pageNumber = 0;
            var pageSize = 10;

            // var query = entityManager.createQuery("select a.lastName, count(t) from Training t join t.authors a group by a having count (t) > 1", Object[].class);

            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Object[].class);
            var trainingsRoot = criteriaQuery.from(Training.class);
            var authorsJoin = trainingsRoot.<Training, Person>join("authors");
            criteriaQuery.multiselect(authorsJoin.get("lastName"), criteriaBuilder.count(trainingsRoot))
                    .groupBy(authorsJoin)
                    .having(criteriaBuilder.greaterThan(criteriaBuilder.count(trainingsRoot), 1L));

            var query = entityManager.createQuery(criteriaQuery);

            var result  = query
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

            result.forEach(record -> System.out.println(Arrays.toString(record)));
        });
    }


    @Test
    void test() {
        run(entityManager -> entityManager.persist(client));
        run(entityManager -> {
            entityManager.find(Client.class, client.getId());
            var cacheManager = CacheManager.ALL_CACHE_MANAGERS.get(0);
            int cacheSize = cacheManager.getCache("pl.training.jpa.Client").getSize();
            assertEquals(1, cacheSize);
        });
        run(entityManager -> {
            entityManager.find(Client.class, client.getId());
            assertEquals(2, STATISTICS.getSecondLevelCacheHitCount());
        });
        System.out.println("#################################################");
        run(entityManager -> {
            assertEquals(0, getQueriesCacheSize());
            entityManager.createQuery("select c from Client c")
                    .setHint("org.hibernate.cacheable", true)
                    .getResultList();
            assertEquals(1, getQueriesCacheSize());
        });
        System.out.println("#################################################");
        run(entityManager -> {
            assertEquals(1, getQueriesCacheSize());
            entityManager.createQuery("select c from Client c")
                    .setHint("org.hibernate.cacheable", true)
                    .getResultList();
            assertEquals(1, getQueriesCacheSize());
        });
    }

    private int getQueriesCacheSize() {
        var cacheManager = CacheManager.ALL_CACHE_MANAGERS.get(0);
        return cacheManager.getCache("default-query-results-region").getSize();
    }

}
