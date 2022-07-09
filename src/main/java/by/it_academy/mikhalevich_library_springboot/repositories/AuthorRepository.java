package by.it_academy.mikhalevich_library_springboot.repositories;

import by.it_academy.mikhalevich_library_springboot.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuthorRepository extends JpaRepository<Author, Integer>, JpaSpecificationExecutor<Author> {
}