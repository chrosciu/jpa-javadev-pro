package pl.training.jpa.shop.payments.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PaymentService {

    private final PaymentsReaderAdapter paymentsReader;

    PaymentDomain getById(String id) {
        return paymentsReader.getById(id)
                .orElseThrow(PaymentNotFoundException::new);
    }

}
