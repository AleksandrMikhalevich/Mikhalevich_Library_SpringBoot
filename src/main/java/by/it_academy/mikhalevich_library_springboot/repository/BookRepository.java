package by.it_academy.mikhalevich_library_springboot.repository;

import by.it_academy.mikhalevich_library_springboot.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
}