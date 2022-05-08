package pl.training.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Account {

    @GenericGenerator(name = "uuid", strategy = "pl.training.jpa.UuidGenerator")
    @GeneratedValue(generator = "uuid")
    //@GeneratedValue
    @Id
    private UUID id;
    @AccountNumber(length = 10, groups = Extended.class)
    //@Pattern(regexp = "\\d{26}")
    @NotNull(groups = Base.class)
    @Column(name = "account_number")
    private String number;
    @Min(value = 1, groups = {Base.class, Extended.class})
    private long value;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Account account = (Account) otherObject;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
