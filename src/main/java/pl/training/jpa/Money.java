package pl.training.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@Data
public class Money implements Serializable {

    private BigDecimal value;
    @Column(name = "currency", length = 5)
    private String currencyCode;

}
