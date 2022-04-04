package pl.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.function.Consumer;

class TestUtils {

    static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("training");

    static void run(Consumer<EntityManager> task) {
        var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        var transaction = entityManager.getTransaction();
        transaction.begin();
        task.accept(entityManager);
        transaction.commit();
        entityManager.close();
    }

}
