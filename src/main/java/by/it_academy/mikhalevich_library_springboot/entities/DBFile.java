package by.it_academy.mikhalevich_library_springboot.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-22 14:56
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "file")
public class DBFile {

    @Id
    @GenericGenerator(name = "one-to-one",
            strategy = "foreign",
            parameters = @Parameter(name = "property", value = "book"))
    @GeneratedValue(generator = "one-to-one")
    @Column(name = "book_id")
    private Integer id;

    @Column
    private String fileName;

    @Column
    private String fileType;

    @Lob
    @ToString.Exclude
    private byte[] data;

    @OneToOne
    @PrimaryKeyJoinColumn
    @ToString.Exclude
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBFile)) return false;
        DBFile dbFile = (DBFile) o;
        return Objects.equals(getId(), dbFile.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
