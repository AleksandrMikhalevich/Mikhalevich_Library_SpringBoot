package by.it_academy.mikhalevich_library_springboot.entities;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Book> books = new HashSet<>();

    @ManyToMany(mappedBy = "publishers")
    @ToString.Exclude
    private Set<Author> authors = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publisher publisher = (Publisher) o;
        return Objects.equals(id, publisher.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void removeAuthor(Author author) {
        this.authors.remove(author);
        author.getPublishers().remove(this);
    }
}