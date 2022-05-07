package pl.training.jpa;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "contract_employee")
// @DiscriminatorValue("contract")
@Entity
@Getter
@Setter
public class ContractEmployee extends Employee {

    private String contractType;

}
