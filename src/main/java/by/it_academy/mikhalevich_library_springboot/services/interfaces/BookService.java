package by.it_academy.mikhalevich_library_springboot.services.interfaces;

import by.it_academy.mikhalevich_library_springboot.search_filters.BookFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import org.springframework.data.domain.Page;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 16:01
 */
public interface BookService {

    Page<BookDto> findAllPaginatedSortedFiltered(BookFilter bookFilter, int pageNumber, int pageSize, String sortField, String sortDirection);

    BookDto findBookById(int id);

    void addBook(BookDto bookDto);

    void updateBook(BookDto bookDto);

    void deleteBookById(int id);

}
