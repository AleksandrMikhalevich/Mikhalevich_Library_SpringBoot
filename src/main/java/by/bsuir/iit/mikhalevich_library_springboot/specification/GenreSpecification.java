package by.bsuir.iit.mikhalevich_library_springboot.specification;

import by.bsuir.iit.mikhalevich_library_springboot.entity.Book;
import by.bsuir.iit.mikhalevich_library_springboot.entity.Book_;
import by.bsuir.iit.mikhalevich_library_springboot.entity.Genre;
import by.bsuir.iit.mikhalevich_library_springboot.entity.Genre_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 17:38
 */
public class GenreSpecification {

    private GenreSpecification() {
    }

    public static Specification<Genre> getGenreByNameSpec(String name) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (name != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Genre_.NAME), "%" + name + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Genre> getGenreByDescriptionSpec(String description) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (description != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Genre_.DESCRIPTION), "%" + description + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Genre> getGenreByBookTitleSpec(String bookTitle) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (bookTitle != null) {
                query.distinct(true);
                SetJoin<Genre, Book> inBooks = root.join(Genre_.books);
                predicatesMain.add(criteriaBuilder.like(inBooks.get(Book_.title.getName()), "%" + bookTitle + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }
}
