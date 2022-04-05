package pl.training.jpa.shop.payments.adapters.persistence;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentRepository {

    private final TransactionTemplate transactionTemplate;

    public PaymentEntity getById(String id) {
        return transactionTemplate.run(entityManager -> entityManager.find(PaymentEntity.class, id));
    }

}
