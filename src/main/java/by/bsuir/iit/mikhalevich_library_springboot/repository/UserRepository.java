package by.bsuir.iit.mikhalevich_library_springboot.repository;

import by.bsuir.iit.mikhalevich_library_springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findByLogin(String login);
}