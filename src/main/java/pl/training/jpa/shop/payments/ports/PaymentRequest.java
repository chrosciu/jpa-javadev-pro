package pl.training.jpa.shop.payments.ports;

import lombok.Value;

@Value
public class PaymentRequest {

    String id;
    String value;

}
