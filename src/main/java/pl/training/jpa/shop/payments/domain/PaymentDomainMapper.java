package pl.training.jpa.shop.payments.domain;

import org.mapstruct.Mapper;
import pl.training.jpa.shop.commons.FastMoneyMapper;
import pl.training.jpa.shop.payments.ports.Payment;
import pl.training.jpa.shop.payments.ports.PaymentId;

@Mapper(uses = FastMoneyMapper.class)
interface PaymentDomainMapper {

    PaymentDomain toDomain(Payment payment);

    String toDomain(PaymentId id);

    Payment toContract(PaymentDomain payment);

    PaymentId toContract(String id);

}
