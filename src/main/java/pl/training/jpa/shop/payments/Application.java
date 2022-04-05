package pl.training.jpa.shop.payments;

import jakarta.persistence.Persistence;
import org.mapstruct.factory.Mappers;
import pl.training.jpa.shop.payments.adapters.persistence.*;
import pl.training.jpa.shop.payments.domain.DefaultPaymentsFactory;
import pl.training.jpa.shop.payments.ports.PaymentId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Application {

    public static void main(String[] args) {
        var entityManagerFactory = Persistence.createEntityManagerFactory("training");
        var transactionTemplate = new TransactionTemplate(entityManagerFactory);
        var paymentRepository = new PaymentRepository(transactionTemplate);
        var paymentPersistenceMapper = Mappers.getMapper(PaymentPersistenceMapper.class);
        var paymentsReader = new PaymentReaderAdapter(paymentRepository, paymentPersistenceMapper);

        var paymentsFactory = new DefaultPaymentsFactory();
        var getPaymentUseCase = paymentsFactory.getPaymentUseCase(paymentsReader);
        // -------------------------------------------------------------------------------------------

        var persistedPayment = transactionTemplate.run(entityManager -> {
           var payment = new PaymentEntity();
           payment.setId(UUID.randomUUID().toString());
           payment.setTimestamp(LocalDateTime.now());
           payment.setValue(BigDecimal.valueOf(1_000));
           payment.setCurrency("PLN");
           entityManager.persist(payment);
           return  payment;
        });

        var paymentId = new PaymentId(UUID.fromString(persistedPayment.getId()));
        System.out.println(getPaymentUseCase.getById(paymentId));
    }

}
