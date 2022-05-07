package pl.training.jpa;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

//@Embeddable
@Data
public class PaymentId implements Serializable {

    private String id;
    private String externalTransactionId;

}
