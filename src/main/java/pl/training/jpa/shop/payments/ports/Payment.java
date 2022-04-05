package pl.training.jpa.shop.payments.ports;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Payment {

    PaymentId id;
    String value;
    LocalDateTime timestamp;

}
