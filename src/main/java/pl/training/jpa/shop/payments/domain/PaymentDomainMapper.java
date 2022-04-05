package pl.training.jpa.shop.payments.domain;

import org.mapstruct.Mapper;
import pl.training.jpa.shop.commons.FastMoneyMapper;
import pl.training.jpa.shop.payments.ports.Payment;
import pl.training.jpa.shop.payments.ports.PaymentId;

import java.util.UUID;

@Mapper(uses = FastMoneyMapper.class)
interface PaymentDomainMapper {

    PaymentDomain toDomain(Payment payment);

    default String toDomain(PaymentId id) {
        return id.getUuid().toString();
    }

    Payment toContract(PaymentDomain payment);

    default PaymentId toContract(String id) {
        return new PaymentId(UUID.fromString(id));
    }

}
