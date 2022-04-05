package pl.training.jpa.shop.payments.adapters.rest;

import lombok.RequiredArgsConstructor;
import pl.training.jpa.shop.payments.ports.GetPaymentUseCase;

@RequiredArgsConstructor
public class GetPaymentUseCaseRestAdapter {

    private final GetPaymentUseCase getPaymentUseCase;
    private final PaymentRestMapper paymentMapper;

    public PaymentDto getById(String id) {
        var payment = getPaymentUseCase.getById(paymentMapper.toContract(id));
        return paymentMapper.toDto(payment);
    }

}
