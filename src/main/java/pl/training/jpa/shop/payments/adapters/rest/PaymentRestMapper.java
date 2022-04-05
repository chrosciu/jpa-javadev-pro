package pl.training.jpa.shop.payments.adapters.rest;

import org.mapstruct.Mapper;
import pl.training.jpa.shop.payments.ports.Payment;
import pl.training.jpa.shop.payments.ports.PaymentId;

import java.util.UUID;

@Mapper
public interface PaymentRestMapper {

    PaymentDto toDto(Payment payment);

    default PaymentId toContract(String id) {
        return new PaymentId(UUID.fromString(id));
    }

}
