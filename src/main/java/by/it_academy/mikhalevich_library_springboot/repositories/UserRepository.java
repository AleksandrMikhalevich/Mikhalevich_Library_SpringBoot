package by.it_academy.mikhalevich_library_springboot.repositories;

import by.it_academy.mikhalevich_library_springboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByLogin(String login);
}