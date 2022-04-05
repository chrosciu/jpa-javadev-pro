package pl.training.jpa;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.util.List;

@EqualsAndHashCode(of = "id")
@Data
@Entity
public class Module {

    @Id
    private String id;
    private String title;
    @Column(length = 4096)
    private String description;
    @AttributeOverride(name = "value", column = @Column(name = "DURATION"))
    @AttributeOverride(name = "unit", column = @Column(name = "DURATION_UNIT"))
    @Embedded
    private Duration duration;
    @JoinColumn(name = "MODULE_ID")
    @OneToMany
    private List<Module> submodules;

}
