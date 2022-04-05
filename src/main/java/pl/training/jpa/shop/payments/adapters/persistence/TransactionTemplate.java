package pl.training.jpa.shop.payments.adapters.persistence;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class TransactionTemplate {

    private final EntityManagerFactory entityManagerFactory;

    public <T> T run(Function<EntityManager, T> task) {
        var entityManager = entityManagerFactory.createEntityManager();
        var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            var result = task.apply(entityManager);
            transaction.commit();
            return result;
        } finally {
            transaction.setRollbackOnly();
            entityManager.close();
        }
    }

}
