package pl.training.jpa;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@NamedQuery(name = Training.ALL, query = "select t from Training t")
@NamedEntityGraph(name = Training.WITH_ALL, attributeNodes = {
        @NamedAttributeNode("authors"),
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("modules"),
})
@EqualsAndHashCode(of = "id")
@Data
@Entity
public class Training {

    public static final String WITH_ALL = "trainingWithALl";
    public static final String ALL = "trainingAll";

    @Id
    private String id;
    @Column(unique = true)
    private String code;
    @Column(unique = true)
    private String title;
    private String slug;
    @ManyToMany
    private List<Person> authors;
    @ManyToMany
    private Set<Tag> tags;
    @Enumerated(EnumType.STRING)
    private TrainingType type;
    @AttributeOverride(name = "value", column = @Column(name = "DURATION"))
    @AttributeOverride(name = "unit", column = @Column(name = "DURATION_UNIT"))
    @Embedded
    private Duration duration;
    @Enumerated(EnumType.ORDINAL)
    private Difficulty difficulty;
    @Column(length = 4096)
    private String description;
    @JoinColumn(name = "TRAINING_ID")
    @OneToMany
    private List<Module> modules;

    @PrePersist
    public void prePersist() {
        slug = title.toLowerCase().replaceAll("\\s+", "-");
    }

}
