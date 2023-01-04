package by.it_academy.mikhalevich_library_springboot.specifications;

import by.it_academy.mikhalevich_library_springboot.entities.Author;
import by.it_academy.mikhalevich_library_springboot.entities.Author_;
import by.it_academy.mikhalevich_library_springboot.entities.Book;
import by.it_academy.mikhalevich_library_springboot.entities.Book_;
import by.it_academy.mikhalevich_library_springboot.entities.Publisher;
import by.it_academy.mikhalevich_library_springboot.entities.Publisher_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-12 12:45
 */
public class AuthorSpecification {

    public static Specification<Author> getAuthorByFirstNameSpec(String firstName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (firstName != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Author_.FIRST_NAME), "%" + firstName + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Author> getAuthorBySecondNameSpec(String secondName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (secondName != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Author_.SECOND_NAME), "%" + secondName + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Author> getAuthorBySurnameSpec(String surname) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (surname != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Author_.SURNAME), "%" + surname + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Author> getAuthorByCountrySpec(String country) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (country != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Author_.COUNTRY), "%" + country + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Author> getAuthorByBookTitleSpec(String bookTitle) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (bookTitle != null) {
                query.distinct(true);
                SetJoin<Author, Book> inBooks = root.join(Author_.books);
                predicatesMain.add(criteriaBuilder.like(inBooks.get(Book_.title.getName()), "%" + bookTitle + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Author> getAuthorByPublisherNameSpec(String publisherName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (publisherName != null) {
                query.distinct(true);
                SetJoin<Author, Publisher> inPublishers = root.join(Author_.publishers);
                predicatesMain.add(criteriaBuilder.like(inPublishers.get(Publisher_.name.getName()), "%" + publisherName + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }
}
