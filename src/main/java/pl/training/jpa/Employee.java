package pl.training.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

//@Inheritance(strategy = InheritanceType.JOINED)

// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// @DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
// @DiscriminatorValue("full-time")
@Entity
@Getter
@Setter
public class Employee {


    @GeneratedValue
    @Id
    private Long id;
    private String name;

}
