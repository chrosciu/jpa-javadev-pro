package pl.training.jpa.shop.payments.adapters.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.training.jpa.shop.payments.ports.Payment;
import pl.training.jpa.shop.payments.ports.PaymentId;

import java.util.UUID;

@Mapper
public interface PaymentPersistenceMapper {

    @Mapping(target = "value", expression = "java(payment.getValue() + \" \" + payment.getCurrency())")
    Payment toContract(PaymentEntity payment);

    default PaymentId toContract(String id) {
        return new PaymentId(UUID.fromString(id));
    }

    default String toEntity(PaymentId id) {
        return id.getUuid().toString();
    }

}
