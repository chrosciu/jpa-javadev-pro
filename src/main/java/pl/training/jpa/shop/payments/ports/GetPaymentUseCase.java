package pl.training.jpa.shop.payments.ports;

public interface GetPaymentUseCase {

   Payment getById(PaymentId id);

}
