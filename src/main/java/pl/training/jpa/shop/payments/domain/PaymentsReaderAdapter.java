package pl.training.jpa.shop.payments.domain;

import lombok.RequiredArgsConstructor;
import pl.training.jpa.shop.payments.ports.PaymentReader;

import java.util.Optional;

@RequiredArgsConstructor
class PaymentsReaderAdapter {

    private final PaymentReader paymentReader;
    private final PaymentsDomainMapper paymentsMapper;

    Optional<PaymentDomain> getById(String id) {
        return paymentReader.getById(paymentsMapper.toContract(id))
                .map(paymentsMapper::toDomain);
    }

}
