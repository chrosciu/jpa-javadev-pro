package pl.training.jpa.shop.payments.ports;

public interface ProcessPaymentUseCase {

    Payment process(PaymentRequest paymentRequest);

}
