package by.bsuir.iit.mikhalevich_library_springboot.service.impl;

import by.bsuir.iit.mikhalevich_library_springboot.entity.Book;
import by.bsuir.iit.mikhalevich_library_springboot.filter.BookFilter;
import by.bsuir.iit.mikhalevich_library_springboot.repository.AuthorRepository;
import by.bsuir.iit.mikhalevich_library_springboot.repository.BookRepository;
import by.bsuir.iit.mikhalevich_library_springboot.repository.GenreRepository;
import by.bsuir.iit.mikhalevich_library_springboot.repository.PublisherRepository;
import by.bsuir.iit.mikhalevich_library_springboot.service.dto.BookDto;
import by.bsuir.iit.mikhalevich_library_springboot.service.interf.BookService;
import by.bsuir.iit.mikhalevich_library_springboot.specification.BookSpecification;
import by.bsuir.iit.mikhalevich_library_springboot.service.mapper.AdditionalMapper;
import by.bsuir.iit.mikhalevich_library_springboot.service.mapper.AuthorMapper;
import by.bsuir.iit.mikhalevich_library_springboot.service.mapper.BookMapper;
import by.bsuir.iit.mikhalevich_library_springboot.service.mapper.GenreMapper;
import by.bsuir.iit.mikhalevich_library_springboot.service.mapper.PublisherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public Page<BookDto> findAllBooksPaginatedSortedFiltered(BookFilter bookFilter, int pageNumber, int pageSize,
                                                             String sortField, String sortDirection) {
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
