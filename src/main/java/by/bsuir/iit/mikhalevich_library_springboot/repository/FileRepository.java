package by.bsuir.iit.mikhalevich_library_springboot.repository;

import by.bsuir.iit.mikhalevich_library_springboot.entity.DBFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<DBFile, Integer> {
}