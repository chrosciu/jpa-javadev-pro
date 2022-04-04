package pl.training.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

// https://vladmihalcea.com/hibernate-facts-equals-and-hashcode
// https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier
@Entity
@Getter
@Setter
public class Client {

    @GeneratedValue
    @Id
    private Long id;
    private String firstName;
    private String lastName;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Client client = (Client) otherObject;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
