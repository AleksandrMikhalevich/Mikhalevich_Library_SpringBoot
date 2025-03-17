package by.bsuir.iit.mikhalevich_library_springboot.repository;

import by.bsuir.iit.mikhalevich_library_springboot.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer>, JpaSpecificationExecutor<Publisher> {
}