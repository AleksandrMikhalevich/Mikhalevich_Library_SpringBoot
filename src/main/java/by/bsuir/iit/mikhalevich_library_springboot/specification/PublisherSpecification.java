package by.bsuir.iit.mikhalevich_library_springboot.specification;

import by.bsuir.iit.mikhalevich_library_springboot.entity.Address;
import by.bsuir.iit.mikhalevich_library_springboot.entity.Address_;
import by.bsuir.iit.mikhalevich_library_springboot.entity.Publisher;
import by.bsuir.iit.mikhalevich_library_springboot.entity.Publisher_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 18:31
 */
public class PublisherSpecification {

    private PublisherSpecification() {
    }

    public static Specification<Publisher> getPublisherByNameSpec(String name) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (name != null) {
                predicatesMain.add(criteriaBuilder.like(root.get(Publisher_.NAME), "%" + name + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Publisher> getPublisherByCountrySpec(String country) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (country != null) {
                Join<Publisher, Address> inAddress = root.join(Publisher_.address);
                predicatesMain.add(criteriaBuilder.like(inAddress.get(Address_.country.getName()), "%" + country + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Publisher> getPublisherByCitySpec(String city) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (city != null) {
                Join<Publisher, Address> inAddress = root.join(Publisher_.address);
                predicatesMain.add(criteriaBuilder.like(inAddress.get(Address_.city.getName()), "%" + city + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Publisher> getPublisherByStreetSpec(String street) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (street != null) {
                Join<Publisher, Address> inAddress = root.join(Publisher_.address);
                predicatesMain.add(criteriaBuilder.like(inAddress.get(Address_.street.getName()), "%" + street + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Publisher> getPublisherByHouseSpec(String house) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (house != null) {
                Join<Publisher, Address> inAddress = root.join(Publisher_.address);
                predicatesMain.add(criteriaBuilder.like(inAddress.get(Address_.house.getName()), "%" + house + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }

    public static Specification<Publisher> getPublisherByZipcodeSpec(String zipcode) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesMain = new ArrayList<>();
            if (zipcode != null) {
                Join<Publisher, Address> inAddress = root.join(Publisher_.address);
                predicatesMain.add(criteriaBuilder.like(inAddress.get(Address_.zipcode.getName()), "%" + zipcode + "%"));
            }
            return criteriaBuilder.and(predicatesMain.toArray(new Predicate[0]));
        };
    }
}
