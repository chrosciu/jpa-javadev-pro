package pl.training.jpa;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Embeddable
@Data
public class Duration {

    private Long value;
    @Enumerated(EnumType.STRING)
    private DurationUnit unit;

}
