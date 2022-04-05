package pl.training.jpa.shop.payments.ports;

import lombok.Value;

import java.util.UUID;

@Value
public class PaymentId {

    UUID uuid;

}
