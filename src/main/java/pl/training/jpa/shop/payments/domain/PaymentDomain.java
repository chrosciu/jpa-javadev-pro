package pl.training.jpa.shop.payments.domain;

import lombok.Value;
import org.javamoney.moneta.FastMoney;

import java.time.LocalDateTime;

@Value
class PaymentDomain {

    String id;
    FastMoney value;
    LocalDateTime timestamp;

}
