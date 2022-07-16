package by.it_academy.mikhalevich_library_springboot.specifications;

import by.it_academy.mikhalevich_library_springboot.entities.Book;
import by.it_academy.mikhalevich_library_springboot.entities.Book_;
import by.it_academy.mikhalevich_library_springboot.entities.Publisher;
import by.it_academy.mikhalevich_library_springboot.entities.Publisher_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 18:31
 */
public class PublisherSpecification {

    public static Specification<Publisher> getPublisherByNameSpec(String name) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (name != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Publisher_.NAME), "%" + name + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }
}
