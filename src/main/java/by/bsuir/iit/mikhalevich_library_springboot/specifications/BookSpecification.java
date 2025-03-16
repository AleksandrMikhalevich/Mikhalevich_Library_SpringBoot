package by.bsuir.iit.mikhalevich_library_springboot.specifications;

import by.bsuir.iit.mikhalevich_library_springboot.entities.Author;
import by.bsuir.iit.mikhalevich_library_springboot.entities.Author_;
import by.bsuir.iit.mikhalevich_library_springboot.entities.Book;
import by.bsuir.iit.mikhalevich_library_springboot.entities.Book_;
import by.bsuir.iit.mikhalevich_library_springboot.entities.Genre;
import by.bsuir.iit.mikhalevich_library_springboot.entities.Genre_;
import by.bsuir.iit.mikhalevich_library_springboot.entities.Publisher;
import by.bsuir.iit.mikhalevich_library_springboot.entities.Publisher_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 17:48
 */
public class BookSpecification {

    private BookSpecification() {
    }

    public static Specification<Book> getBookByTitleSpec(String title) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (title != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Book_.TITLE), "%" + title + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> getBookByLanguageSpec(String language) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (language != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Book_.LANGUAGE), "%" + language + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> getBookBySummarySpec(String summary) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (summary != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Book_.SUMMARY), "%" + summary + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> getBookByAuthorNameSpec(String authorName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (authorName != null) {
                query.distinct(true);
                SetJoin<Book, Author> inAuthors = root.join(Book_.authors);
                predicatesMain.add(criteriaBuilder.or(criteriaBuilder.like(inAuthors.get(Author_.firstName.getName()), "%" + authorName + "%"),
                        criteriaBuilder.like(inAuthors.get(Author_.secondName.getName()), "%" + authorName + "%"),
                        criteriaBuilder.like(inAuthors.get(Author_.surname.getName()), "%" + authorName + "%")));

            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> getBookByGenreNameSpec(String genreName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (genreName != null) {
                query.distinct(true);
                SetJoin<Book, Genre> inGenres = root.join(Book_.genres);
                predicatesMain.add(criteriaBuilder.like(inGenres.get(Genre_.name.getName()), "%" + genreName + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> getBookByPublisherNameSpec(String publisherName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (publisherName != null) {
                Join<Book, Publisher> inPublishers = root.join(Book_.publisher);
                predicatesMain.add(criteriaBuilder.like(inPublishers.get(Publisher_.name.getName()), "%" + publisherName + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> getBookByReceiptDateSpec(String fromDate, String toDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (fromDate != null && toDate != null && !Objects.equals(fromDate, "") && !Objects.equals(toDate, "")) {
                Date from = Date.valueOf(fromDate);
                Date to = Date.valueOf(toDate);
                predicatesMain.add(criteriaBuilder.between(root.get(Book_.RECEIPT_DATE), from, to));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> getBookByYearOfPublishingSpec(String fromYear, String toYear) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (fromYear != null && toYear != null && !Objects.equals(fromYear, "") && !Objects.equals(toYear, "")) {
                int numFromYear = Integer.parseInt(fromYear);
                int numToYear = Integer.parseInt(toYear);
                predicatesMain.add(criteriaBuilder.between(root.get(Book_.YEAR_OF_PUBLISHING), numFromYear, numToYear));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }
}
