package pl.training.jpa.shop.payments.ports;

public interface PaymentsFactory {

    GetPaymentUseCase getPaymentUseCase(PaymentReader paymentReader);

}
