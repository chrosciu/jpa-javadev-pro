package pl.training.jpa.shop.payments.domain;

import lombok.RequiredArgsConstructor;
import pl.training.jpa.shop.payments.ports.GetPaymentUseCase;
import pl.training.jpa.shop.payments.ports.Payment;
import pl.training.jpa.shop.payments.ports.PaymentId;

@RequiredArgsConstructor
class GetPaymentUseCaseAdapter implements GetPaymentUseCase {

    private final PaymentService paymentService;
    private final PaymentDomainMapper paymentMapper;

    @Override
    public Payment getById(PaymentId id) {
        var paymentDomain = paymentService.getById(paymentMapper.toDomain(id));
        return paymentMapper.toContract(paymentDomain);
    }

}
