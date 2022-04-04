package pl.training.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Author {

    @Id
    private String id;
    private String fullName;
    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

}
