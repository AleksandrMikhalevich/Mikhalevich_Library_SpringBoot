package by.it_academy.mikhalevich_library_springboot.services.interfaces;

import by.it_academy.mikhalevich_library_springboot.filters.AuthorFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-12 12:20
 */
public interface AuthorService {

    Page<AuthorDto> findAllAuthorsPaginatedSortedFiltered(AuthorFilter authorFilter, int pageNumber, int pageSize, String sortField, String sortDirection);

    List<AuthorDto> findAllAuthors();

    AuthorDto findAuthorById(int id);

    void addAuthor(AuthorDto authorDto);

    void updateAuthor(AuthorDto authorDto);

    void deleteAuthorById(int id);

    List<AuthorDto> chooseAuthors(Integer[] authorsIds);

}
