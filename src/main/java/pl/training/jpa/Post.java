package pl.training.jpa;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@NamedEntityGraph(name = Post.WITH_TAGS, attributeNodes = @NamedAttributeNode("tags"))
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Post {

    public static final String WITH_TAGS = "postWithTags";

    @Id
    private String id;
    private String title;
    // @Basic(fetch = FetchType.LAZY)
    // @Lob
    private String content;
    @JoinColumn(name = "post_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;
    @JoinTable(name = "post_tag", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags;

}
