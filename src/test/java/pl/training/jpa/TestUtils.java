package pl.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import java.util.function.Consumer;

public class TestUtils {

    static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("training-test");

    static final Statistics STATISTICS = ENTITY_MANAGER_FACTORY.unwrap(SessionFactory.class).getStatistics();

    static void run(Consumer<EntityManager> task) {
        var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        var transaction = entityManager.getTransaction();
        transaction.begin();
        task.accept(entityManager);
        transaction.commit();
        entityManager.close();
    }

    static void runAsync(Consumer<EntityManager> task) {
        new Thread(() -> run(task)).start();
    }

    static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    static void measure(Runnable runnable) {
        var startTime = System.currentTimeMillis();
        runnable.run();
        System.out.printf("Total time: %d ms\n", System.currentTimeMillis() - startTime);
    }

}
