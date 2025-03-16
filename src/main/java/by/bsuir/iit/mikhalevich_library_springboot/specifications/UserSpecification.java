package by.bsuir.iit.mikhalevich_library_springboot.specifications;

import by.bsuir.iit.mikhalevich_library_springboot.entities.User;
import by.bsuir.iit.mikhalevich_library_springboot.entities.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-31 13:27
 */
public class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> getUserByLoginSpec(String login) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (login != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(User_.LOGIN), "%" + login + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> getUserByEmailSpec(String email) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (email != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(User_.EMAIL), "%" + email + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }
}
