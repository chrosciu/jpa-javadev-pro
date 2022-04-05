package pl.training.jpa.shop.payments.adapters.persistence;

import lombok.RequiredArgsConstructor;
import pl.training.jpa.shop.payments.ports.Payment;
import pl.training.jpa.shop.payments.ports.PaymentId;
import pl.training.jpa.shop.payments.ports.PaymentReader;

import java.util.Optional;

@RequiredArgsConstructor
public class PaymentReaderAdapter implements PaymentReader {

    private final PaymentRepository paymentRepository;
    private final PaymentPersistenceMapper paymentMapper;

    @Override
    public Optional<Payment> getById(PaymentId id) {
       var paymentEntity = paymentRepository.getById(paymentMapper.toEntity(id));
       return  Optional.ofNullable(paymentEntity)
               .map(paymentMapper::toContract);
    }

}
