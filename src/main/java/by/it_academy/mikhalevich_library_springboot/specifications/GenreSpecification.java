package by.it_academy.mikhalevich_library_springboot.specifications;

import by.it_academy.mikhalevich_library_springboot.entities.Author_;
import by.it_academy.mikhalevich_library_springboot.entities.Genre;
import by.it_academy.mikhalevich_library_springboot.entities.Genre_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 17:38
 */
public class GenreSpecification {

    public static Specification<Genre> getGenreByNameSpec(String name) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (name != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Genre_.NAME), "%" + name + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }
}
