package by.it_academy.mikhalevich_library_springboot.services.impl;

import by.it_academy.mikhalevich_library_springboot.entities.Book;
import by.it_academy.mikhalevich_library_springboot.repositories.AuthorRepository;
import by.it_academy.mikhalevich_library_springboot.repositories.BookRepository;
import by.it_academy.mikhalevich_library_springboot.filters.BookFilter;
import by.it_academy.mikhalevich_library_springboot.repositories.GenreRepository;
import by.it_academy.mikhalevich_library_springboot.repositories.PublisherRepository;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.BookService;
import by.it_academy.mikhalevich_library_springboot.services.mappers.*;
import by.it_academy.mikhalevich_library_springboot.specifications.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 17:46
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final PublisherRepository publisherRepository;
    private final BookMapper bookMapper;
    private final AuthorMapper authorMapper;
    private final GenreMapper genreMapper;
    private final PublisherMapper publisherMapper;
    private final AdditionalMapper additionalMapper;

    @Override
    public Page<BookDto> findAllBooksPaginatedSortedFiltered(BookFilter bookFilter, int pageNumber, int pageSize, String sortField, String sortDirection) {
        Specification<Book> bookSpecification =
                Specification
                        .where(Optional.ofNullable(bookFilter.getTitleFilter())
                                .map(BookSpecification::getBookByTitleSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(bookFilter.getLanguageFilter())
                                .map(BookSpecification::getBookByLanguageSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(bookFilter.getSummaryFilter())
                                .map(BookSpecification::getBookBySummarySpec)
                                .orElse(null))
                        .and(Optional.ofNullable(bookFilter.getAuthorFilter())
                                .map(BookSpecification::getBookByAuthorNameSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(bookFilter.getGenreFilter())
                                .map(BookSpecification::getBookByGenreNameSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(bookFilter.getPublisherFilter())
                                .map(BookSpecification::getBookByPublisherNameSpec)
                                .orElse(null))
                        .and(BookSpecification.getBookByReceiptDateSpec(bookFilter.getReceiptDateFromFilter(),
                                bookFilter.getReceiptDateToFilter()))
                        .and(BookSpecification.getBookByYearOfPublishingSpec(bookFilter.getYearOfPublishingFromFilter(),
                                bookFilter.getYearOfPublishingToFilter()));
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable paged = PageRequest.of(pageNumber - 1, pageSize, sort);
        return bookRepository.findAll(bookSpecification, paged).map(bookMapper::bookToBookDto);
    }

    @Override
    public BookDto findBookById(Integer id) {
        return bookRepository.findById(id).map(bookMapper::bookToBookDto).orElse(null);
    }

    @Override
    @Transactional
    public void addBook(Integer[] authorsIds, Integer[] genresIds, Integer publisherId, String title, String language, String summary,
                        Date receiptDate, String yearOfPublishing) {
        List<Integer> listOfAuthorIds = new ArrayList<>();
        Collections.addAll(listOfAuthorIds, authorsIds);
        Set<BookDto.AuthorDto> authorDtoSet = authorRepository.findAllById(listOfAuthorIds).stream()
                .map(authorMapper::authorToAuthorDto)
                .map(additionalMapper::toBookAuthorDto)
                .collect(Collectors.toSet());
        List<Integer> listOfGenresIds = new ArrayList<>();
        Collections.addAll(listOfGenresIds, genresIds);
        Set<BookDto.GenreDto> genreDtoSet = genreRepository.findAllById(listOfGenresIds).stream()
                .map(genreMapper::genreToGenreDto)
                .map(additionalMapper::toBookGenreDto)
                .collect(Collectors.toSet());
        BookDto.PublisherDto publisherDto = publisherRepository.findById(publisherId)
                .map(publisherMapper::publisherToPublisherDto)
                .map(additionalMapper::toBookPublisherDto).orElse(null);
        BookDto bookDto = BookDto.builder()
                .title(title)
                .language(language)
                .summary(summary)
                .authors(authorDtoSet)
                .genres(genreDtoSet)
                .publisher(publisherDto)
                .receiptDate(receiptDate)
                .yearOfPublishing(yearOfPublishing)
                .build();
        bookRepository.save(bookMapper.bookDtoToBook(bookDto));
    }

    @Override
    @Transactional
    public void updateBook(Integer id, Integer[] authorsIds, Integer[] genresIds, Integer publisherId, String title, String language, String summary,
                           Date receiptDate, String yearOfPublishing) {
        List<Integer> listOfAuthorIds = new ArrayList<>();
        Collections.addAll(listOfAuthorIds, authorsIds);
        Set<BookDto.AuthorDto> authorDtoSet = authorRepository.findAllById(listOfAuthorIds).stream()
                .map(authorMapper::authorToAuthorDto)
                .map(additionalMapper::toBookAuthorDto)
                .collect(Collectors.toSet());
        List<Integer> listOfGenresIds = new ArrayList<>();
        Collections.addAll(listOfGenresIds, genresIds);
        Set<BookDto.GenreDto> genreDtoSet = genreRepository.findAllById(listOfGenresIds).stream()
                .map(genreMapper::genreToGenreDto)
                .map(additionalMapper::toBookGenreDto)
                .collect(Collectors.toSet());
        BookDto.PublisherDto publisherDto = publisherRepository.findById(publisherId)
                .map(publisherMapper::publisherToPublisherDto)
                .map(additionalMapper::toBookPublisherDto).orElse(null);
        BookDto bookDto = BookDto.builder()
                .id(id)
                .title(title)
                .language(language)
                .summary(summary)
                .authors(authorDtoSet)
                .genres(genreDtoSet)
                .publisher(publisherDto)
                .receiptDate(receiptDate)
                .yearOfPublishing(yearOfPublishing)
                .build();
        bookRepository.save(bookMapper.bookDtoToBook(bookDto));
    }

    @Override
    public void deleteBookById(Integer id) {
        bookRepository.deleteById(id);
    }
}
