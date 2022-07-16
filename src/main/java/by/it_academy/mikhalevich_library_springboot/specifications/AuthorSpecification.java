package by.it_academy.mikhalevich_library_springboot.specifications;

import by.it_academy.mikhalevich_library_springboot.entities.Author;
import by.it_academy.mikhalevich_library_springboot.entities.Author_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
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
}
