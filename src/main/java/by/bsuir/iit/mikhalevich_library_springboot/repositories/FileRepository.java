package by.bsuir.iit.mikhalevich_library_springboot.repositories;

import by.bsuir.iit.mikhalevich_library_springboot.entities.DBFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<DBFile, Integer> {
}