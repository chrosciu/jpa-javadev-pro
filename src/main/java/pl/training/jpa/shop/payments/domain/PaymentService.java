package pl.training.jpa.shop.payments.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PaymentService {

    private final PaymentReaderAdapter paymentReader;

    PaymentDomain getById(String id) {
        return paymentReader.getById(id)
                .orElseThrow(PaymentNotFoundException::new);
    }

}
