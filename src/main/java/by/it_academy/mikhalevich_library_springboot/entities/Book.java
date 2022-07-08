package by.it_academy.mikhalevich_library_springboot.entities;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
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
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column
    private String title;

    @Column
    private String language;

    @Column
    private String summary;

    @ManyToMany
    @JoinTable(name = "book_author",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id")})
    @ToString.Exclude
    private Set<Author> authors = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "book_genre",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id")})
    @ToString.Exclude
    private Set<Genre> genres = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    @ToString.Exclude
    private Publisher publisher;

    @Column(name = "year_of_publishing")
    private String yearOfPublishing;

    @Column(name = "receipt_date")
    private Date receiptDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}