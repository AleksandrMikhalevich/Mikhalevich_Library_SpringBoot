package by.it_academy.mikhalevich_library_springboot.controller;

import by.it_academy.mikhalevich_library_springboot.filters.AuthorFilter;
import by.it_academy.mikhalevich_library_springboot.filters.BookFilter;
import by.it_academy.mikhalevich_library_springboot.filters.GenreFilter;
import by.it_academy.mikhalevich_library_springboot.filters.PublisherFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.GenreDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.PublisherDto;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.AuthorService;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.BookService;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.GenreService;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.sql.Date;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 18:00
 */
@Controller
@RequiredArgsConstructor
@SessionAttributes(value = {"bookAuthors", "bookGenres", "bookPublisher", "book", "page", "size", "field", "dir"})
public class AppController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final PublisherService publisherService;

    @GetMapping(value = {"/", "/index"})
    public String showMain() {
        return "index";
    }

    @GetMapping("/books")
    public String showBooksFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return showAllBooks(1, 5, "id", "asc", null,
                null, null, null, null, null, model, sessionStatus);
    }

    @GetMapping("/books/page/{pageNumber}")
    public String showAllBooks(@PathVariable("pageNumber") int pageNumber, int pageSize,
                               String sortField, String sortDir, String titleFilter, String languageFilter,
                               String summaryFilter, String authorFilter, String genreFilter, String publisherFilter,
                               Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        BookFilter bookFilter = BookFilter.builder()
                .titleFilter(titleFilter)
                .languageFilter(languageFilter)
                .summaryFilter(summaryFilter)
                .authorFilter(authorFilter)
                .genreFilter(genreFilter)
                .publisherFilter(publisherFilter)
                .build();
        Page<BookDto> page = bookService.findAllBooksPaginatedSortedFiltered(bookFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<BookDto> books = page.getContent();
        doPaginationSortingFiltration(pageNumber, pageSize, sortField, sortDir, model, totalPages, totalItems);
        model.addAttribute("titleFilter", titleFilter);
        model.addAttribute("languageFilter", languageFilter);
        model.addAttribute("summaryFilter", summaryFilter);
        model.addAttribute("authorFilter", authorFilter);
        model.addAttribute("genreFilter", genreFilter);
        model.addAttribute("publisherFilter", publisherFilter);
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/books/new-book")
    public String newBook() {
        return "add-book";
    }

    @GetMapping("/books/new-book/choose-authors")
    public String chooseAuthorsForNewBook(Model model) {
        return showAuthorsForNewBook(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/new-book/choose-authors/page/{pageNumber}")
    public String showAuthorsForNewBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                        String sortDir, String firstNameFilter, Model model) {
        getAuthorsForBook(pageNumber, pageSize, sortField, sortDir, firstNameFilter, model);
        return "new-book-choose-authors";
    }

    @GetMapping("/books/new-book/choose-authors/confirm")
    public String confirmAuthorsForNewBook(Model model, Integer[] authorsIds) {
        List<AuthorDto> authorDtoList = authorService.chooseAuthors(authorsIds);
        model.addAttribute("bookAuthors", authorDtoList);
        return "add-book";
    }

    @GetMapping("/books/new-book/choose-genres")
    public String chooseGenresForNewBook(Model model) {
        return showGenresForNewBook(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/new-book/choose-genres/page/{pageNumber}")
    public String showGenresForNewBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                       String sortDir, String nameFilter, Model model) {
        getGenresForBook(pageNumber, pageSize, sortField, sortDir, nameFilter, model);
        return "new-book-choose-genres";
    }

    @GetMapping("/books/new-book/choose-genres/confirm")
    public String confirmGenresForNewBook(Model model, Integer[] genresIds) {
        List<GenreDto> genreDtoList = genreService.chooseGenres(genresIds);
        model.addAttribute("bookGenres", genreDtoList);
        return "add-book";
    }

    @GetMapping("/books/new-book/choose-publisher")
    public String choosePublisherForNewBook(Model model) {
        return showPublishersForNewBook(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/new-book/choose-publisher/page/{pageNumber}")
    public String showPublishersForNewBook(@PathVariable("pageNumber") int pageNumber, int pageSize,
                                           String sortField, String sortDir, String nameFilter, Model model) {
        getPublisherForBook(pageNumber, pageSize, sortField, sortDir, nameFilter, model);
        return "new-book-choose-publisher";
    }

    @GetMapping("/books/new-book/choose-publisher/confirm")
    public String confirmPublisherForNewBook(Model model, int publisherId) {
        PublisherDto publisherDto = publisherService.findPublisherById(publisherId);
        model.addAttribute("bookPublisher", publisherDto);
        return "add-book";
    }

    @PostMapping("/books/add-book")
    public String addBook(Integer[] authorsIds, Integer[] genresIds, Integer publisherId, String title, String language,
                          String summary, Date receiptDate, String yearOfPublishing) {
        bookService.addBook(authorsIds, genresIds, publisherId, title, language, summary, receiptDate, yearOfPublishing);
        return "redirect:/books";
    }

    @PostMapping("/books/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteBook(@PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber,
                             @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                             @PathVariable("sortDir") String sortDir, Model model,  SessionStatus sessionStatus) {
        bookService.deleteBookById(id);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return showAllBooks(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, model, sessionStatus);
    }

    @GetMapping("/books/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editBook(@PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber,
                           @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                           @PathVariable("sortDir") String sortDir, Model model) {
        BookDto bookDto = bookService.findBookById(id);
        model.addAttribute("page", pageNumber);
        model.addAttribute("size", pageSize);
        model.addAttribute("field", sortField);
        model.addAttribute("dir", sortDir);
        model.addAttribute("book", bookDto);
        return "update-book";
    }

    @GetMapping("/books/edit-book/choose-authors")
    public String chooseAuthorsForEditBook(Model model) {
        return showAuthorsForEditBook(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/edit-book/choose-authors/page/{pageNumber}")
    public String showAuthorsForEditBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                         String sortDir, String firstNameFilter, Model model) {
        getAuthorsForBook(pageNumber, pageSize, sortField, sortDir, firstNameFilter, model);
        return "edit-book-choose-authors";
    }

    @GetMapping("/books/edit-book/choose-authors/confirm")
    public String confirmAuthorsForEditBook(Integer[] authorsIds, Model model) {
        List<AuthorDto> authorDtoList = authorService.chooseAuthors(authorsIds);
        model.addAttribute("bookAuthors", authorDtoList);
        return "update-book";
    }

    @GetMapping("/books/edit-book/choose-genres")
    public String chooseGenresForEditBook(Model model) {
        return showGenresForEditBook(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/edit-book/choose-genres/page/{pageNumber}")
    public String showGenresForEditBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                        String sortDir, String nameFilter, Model model) {
        getGenresForBook(pageNumber, pageSize, sortField, sortDir, nameFilter, model);
        return "edit-book-choose-genres";
    }

    @GetMapping("/books/edit-book/choose-genres/confirm")
    public String confirmGenresForEditBook(Model model, Integer[] genresIds) {
        List<GenreDto> genreDtoList = genreService.chooseGenres(genresIds);
        model.addAttribute("bookGenres", genreDtoList);
        return "update-book";
    }

    @GetMapping("/books/edit-book/choose-publisher")
    public String choosePublisherForEditBook(Model model) {
        return showPublishersForEditBook(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/edit-book/choose-publisher/page/{pageNumber}")
    public String showPublishersForEditBook(@PathVariable("pageNumber") int pageNumber, int pageSize,
                                            String sortField, String sortDir, String nameFilter, Model model) {
        getPublisherForBook(pageNumber, pageSize, sortField, sortDir, nameFilter, model);
        return "edit-book-choose-publisher";
    }

    @GetMapping("/books/edit-book/choose-publisher/confirm")
    public String confirmPublisherForEditBook(Model model, int publisherId) {
        PublisherDto publisherDto = publisherService.findPublisherById(publisherId);
        model.addAttribute("bookPublisher", publisherDto);
        return "update-book";
    }

    @PostMapping("/books/update-book/{id}")
    public String updateBook(@PathVariable("id") int id, Integer[] authorsIds, Integer[] genresIds, Integer publisherId, String title, String language,
                             String summary, Date receiptDate, String yearOfPublishing, int pageNumber, int pageSize,
                             String sortField, String sortDir, Model model,  SessionStatus sessionStatus) {
        bookService.updateBook(id, authorsIds, genresIds, publisherId, title, language, summary, receiptDate, yearOfPublishing);
        return showAllBooks(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, model, sessionStatus);
    }


    private void getAuthorsForBook(int pageNumber, int pageSize, String sortField, String sortDir, String firstNameFilter, Model model) {
        AuthorFilter authorFilter = AuthorFilter.builder()
                .firstNameFilter(firstNameFilter)
                .build();
        Page<AuthorDto> page = authorService.findAllAuthorsPaginatedSortedFiltered(authorFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<AuthorDto> authors = page.getContent();
        doPaginationSortingFiltration(pageNumber, pageSize, sortField, sortDir, model, totalPages, totalItems);
        model.addAttribute("authors", authors);
    }

    private void getGenresForBook(int pageNumber, int pageSize, String sortField, String sortDir, String nameFilter, Model model) {
        GenreFilter genreFilter = GenreFilter.builder()
                .nameFilter(nameFilter)
                .build();
        Page<GenreDto> page = genreService.findAllGenresPaginatedSortedFiltered(genreFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<GenreDto> genres = page.getContent();
        doPaginationSortingFiltration(pageNumber, pageSize, sortField, sortDir, model, totalPages, totalItems);
        model.addAttribute("genres", genres);
    }

    private void getPublisherForBook(int pageNumber, int pageSize, String sortField, String sortDir, String nameFilter, Model model) {
        PublisherFilter publisherFilter = PublisherFilter.builder()
                .nameFilter(nameFilter)
                .build();
        Page<PublisherDto> page = publisherService.findAllPublishersPaginatedSortedFiltered(publisherFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<PublisherDto> publishers = page.getContent();
        doPaginationSortingFiltration(pageNumber, pageSize, sortField, sortDir, model, totalPages, totalItems);
        model.addAttribute("publishers", publishers);
    }

    private void doPaginationSortingFiltration(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField, String sortDir, Model model, int totalPages, long totalItems) {
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
    }

}
