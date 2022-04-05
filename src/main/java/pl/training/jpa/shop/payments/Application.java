package pl.training.jpa.shop.payments;

import jakarta.persistence.Persistence;
import org.mapstruct.factory.Mappers;
import pl.training.jpa.shop.payments.adapters.persistence.PaymentPersistenceMapper;
import pl.training.jpa.shop.payments.adapters.persistence.PaymentReaderAdapter;
import pl.training.jpa.shop.payments.adapters.persistence.PaymentRepository;
import pl.training.jpa.shop.payments.adapters.persistence.TransactionTemplate;
import pl.training.jpa.shop.payments.domain.DefaultPaymentsFactory;
import pl.training.jpa.shop.payments.ports.PaymentId;
import pl.training.jpa.shop.payments.ports.PaymentRequest;
import pl.training.jpa.shop.payments.ports.ProcessPaymentUseCase;

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
        ProcessPaymentUseCase processPaymentUseCase;
        // -------------------------------------------------------------------------------------------

        var uuid = UUID.randomUUID();

        processPaymentUseCase.process(new PaymentRequest(uuid.toString(), "100 PLN"));

        var paymentId = new PaymentId(uuid);
        var payment = getPaymentUseCase.getById(paymentId);
        System.out.println(payment);
    }

}
