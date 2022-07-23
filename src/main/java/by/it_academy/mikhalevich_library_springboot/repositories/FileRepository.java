package by.it_academy.mikhalevich_library_springboot.repositories;

import by.it_academy.mikhalevich_library_springboot.entities.DBFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<DBFile, Integer> {
}