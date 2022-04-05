package pl.training.jpa.shop.payments.domain;

import org.mapstruct.Mapper;
import pl.training.jpa.shop.payments.ports.Payment;
import pl.training.jpa.shop.payments.ports.PaymentId;

@Mapper
interface PaymentsDomainMapper {

    PaymentDomain toDomain(Payment payment);

    Payment toContract(PaymentDomain payment);

    PaymentId toContract(String id);

    String toDomain(PaymentId id);

}
