package by.bsuir.iit.mikhalevich_library_springboot.services.interfaces;

import by.bsuir.iit.mikhalevich_library_springboot.filters.BookFilter;
import by.bsuir.iit.mikhalevich_library_springboot.services.dto.BookDto;
import org.springframework.data.domain.Page;

import java.sql.Date;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 16:01
 */
public interface BookService {

    Page<BookDto> findAllBooksPaginatedSortedFiltered(BookFilter bookFilter, int pageNumber, int pageSize, String sortField, String sortDirection);

    BookDto findBookById(Integer id);

    void addBook(Integer[] authorsIds, Integer[] genresIds, Integer publisherId, String title, String language, String summary,
                 Date receiptDate, String yearOfPublishing);

    void updateBook(Integer id, Integer[] authorsIds, Integer[] genresIds, Integer publisherId, String title, String language, String summary,
                    Date receiptDate, String yearOfPublishing);

    void deleteBookById(Integer id);

}
