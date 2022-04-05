package pl.training.jpa.shop.payments.adapters.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.training.jpa.shop.payments.ports.Payment;
import pl.training.jpa.shop.payments.ports.PaymentId;

@Mapper
public interface PaymentPersistenceMapper {

    @Mapping(target = "value", expression = "java(payment.value + ' ' + payment.currency)")
    Payment toContract(PaymentEntity payment);

    String toEntity(PaymentId id);

}
