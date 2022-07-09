package by.it_academy.mikhalevich_library_springboot.services.impl;

import by.it_academy.mikhalevich_library_springboot.entities.Book;
import by.it_academy.mikhalevich_library_springboot.repositories.BookRepository;
import by.it_academy.mikhalevich_library_springboot.search_filters.BookFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.BookService;
import by.it_academy.mikhalevich_library_springboot.services.mappers.BookMapper;
import by.it_academy.mikhalevich_library_springboot.specifications.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 17:46
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public Page<BookDto> findAllPaginatedSortedFiltered(BookFilter bookFilter, int pageNumber, int pageSize, String sortField, String sortDirection) {
        Specification<Book> bookSpecification =
                Specification
                        .where(Optional.ofNullable(bookFilter.getTitleFilter())
                                .map(BookSpecification::getBookByTitleSpec)
                                .orElse(null));
//                        .and(Optional.ofNullable(bookFilter.getLanguageFilter())
//                                .map(BookSpecification::getHorseByPriceSpec)
//                                .orElse(null))
//                        .and(Optional.ofNullable(bookFilter.getSummaryFilter())
//                                .map(BookSpecification::getHorseByTypeSpec)
//                                .orElse(null));
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable paged = PageRequest.of(pageNumber - 1, pageSize, sort);
        return bookRepository.findAll(bookSpecification, paged).map(bookMapper::bookToBookDto);
    }

    @Override
    public BookDto findBookById(int id) {
        Book book = bookRepository.findById(id).orElse(null);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void addBook(BookDto bookDto) {
        bookRepository.save(bookMapper.bookDtoToBook(bookDto));
    }

    @Override
    public void updateBook(BookDto bookDto) {
        bookRepository.save(bookMapper.bookDtoToBook(bookDto));
    }

    @Override
    public void deleteBookById(int id) {
        bookRepository.deleteById(id);
    }
}
