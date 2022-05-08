package pl.training.jpa.shop.payments.ports;

import java.util.Optional;

public interface PaymentReader {

    Optional<Payment> getById(PaymentId id);

}
