package by.it_academy.mikhalevich_library_springboot.repository;

import by.it_academy.mikhalevich_library_springboot.entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PublisherRepository extends JpaRepository<Publisher, Integer>, JpaSpecificationExecutor<Publisher> {
}