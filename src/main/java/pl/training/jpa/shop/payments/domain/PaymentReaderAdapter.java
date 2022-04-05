package pl.training.jpa.shop.payments.domain;

import lombok.RequiredArgsConstructor;
import pl.training.jpa.shop.payments.ports.PaymentReader;

import java.util.Optional;

@RequiredArgsConstructor
class PaymentReaderAdapter {

    private final PaymentReader paymentReader;
    private final PaymentDomainMapper paymentsMapper;

    Optional<PaymentDomain> getById(String id) {
        return paymentReader.getById(paymentsMapper.toContract(id))
                .map(paymentsMapper::toDomain);
    }

}
