package pl.training.jpa.shop.payments.adapters.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class PaymentEntity {

    @Id
    private String id;
    private BigDecimal value;
    private String currency;
    private LocalDateTime timestamp;


}
