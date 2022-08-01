package by.it_academy.mikhalevich_library_springboot.repositories;

import by.it_academy.mikhalevich_library_springboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findByLogin(String login);
}