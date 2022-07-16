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
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 18:00
 */
@Controller
@RequiredArgsConstructor
@SessionAttributes(value = {"bookAuthors", "bookGenres", "bookPublisher"})
public class AppController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final PublisherService publisherService;

    @GetMapping("/")
    public String showMain(Model model) {
        return "index";
    }

    @GetMapping("/books")
    public String showBooksFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return showPaginatedSortedFilteredBooks(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/page/{pageNumber}")
    public String showPaginatedSortedFilteredBooks(@PathVariable("pageNumber") int pageNumber, int pageSize,
                                                   String sortField, String sortDir, String titleFilter, Model model) {
        BookFilter bookFilter = BookFilter.builder()
                .titleFilter(titleFilter)
                .build();
        Page<BookDto> page = bookService.findAllBooksPaginatedSortedFiltered(bookFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<BookDto> books = page.getContent();
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/books/new-book")
    public String newBook() {
        return "add-book";
    }

    @PostMapping("/books/add-book")
    public String addBook(Integer[] authorsIds, Integer[] genresIds, Integer publisherId, String title, String language,
                          String summary, Date receiptDate, String yearOfPublishing) {
        System.out.println(Arrays.toString(authorsIds));
        System.out.println(Arrays.toString(genresIds));
        System.out.println(publisherId);
        System.out.println(title);
        System.out.println(language);
        System.out.println(summary);
        System.out.println(receiptDate);
        System.out.println(yearOfPublishing);
        bookService.addBook(authorsIds, genresIds, publisherId, title, language, summary, receiptDate, yearOfPublishing);
        return "redirect:/books";
    }

    @GetMapping("/books/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editBook(Model model, @PathVariable("id") int id, @PathVariable int pageNumber, @PathVariable int pageSize, @PathVariable String sortField,
                            @PathVariable String sortDir) {
        BookDto bookDto = bookService.findBookById(id);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("book", bookDto);
        return "update-book";
    }

    @PatchMapping("/books/{id}")
    public String updateBook(Model model, @ModelAttribute("book") @Valid BookDto bookDto, int pageNumber, int pageSize, String sortField, String sortDir,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "update-book";
        bookService.updateBook(bookDto);
        return showPaginatedSortedFilteredBooks(pageNumber, pageSize, sortField, sortDir, null, model);
    }

    @DeleteMapping("/books/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteBook(Model model, @PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber, @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                              @PathVariable("sortDir") String sortDir) {
        bookService.deleteBookById(id);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return showPaginatedSortedFilteredBooks(pageNumber, pageSize, sortField, sortDir, null, model);
    }

    @GetMapping("/books/new-book/choose-authors")
    public String chooseAuthorsForBook(Model model) {
        return showPaginatedSortedFilteredAuthors(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/new-book/choose-authors/page/{pageNumber}")
    public String showPaginatedSortedFilteredAuthors(@PathVariable("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize,
                                                   @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir,
                                                   @Param("firstNameFilter") String firstNameFilter, Model model) {
        AuthorFilter authorFilter = AuthorFilter.builder()
                .firstNameFilter(firstNameFilter)
                .build();
        Page<AuthorDto> page = authorService.findAllAuthorsPaginatedSortedFiltered(authorFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<AuthorDto> authors = page.getContent();
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("authors", authors);
        return "choose-authors";
    }

    @GetMapping("/books/new-book/choose-authors/confirm")
    public String confirmAuthorsForBook(Model model, Integer[] authorsIds) {
        List<AuthorDto> authorDtoList = authorService.chooseAuthors(authorsIds);
        model.addAttribute("bookAuthors", authorDtoList);
        return "add-book";
    }

    @GetMapping("/books/new-book/choose-genres")
    public String chooseGenresForBook(Model model) {
        return showPaginatedSortedFilteredGenres(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/new-book/choose-genres/page/{pageNumber}")
    public String showPaginatedSortedFilteredGenres(@PathVariable("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize,
                                                     @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir,
                                                     @Param("nameFilter") String nameFilter, Model model) {
        GenreFilter genreFilter = GenreFilter.builder()
                .nameFilter(nameFilter)
                .build();
        Page<GenreDto> page = genreService.findAllGenresPaginatedSortedFiltered(genreFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<GenreDto> genres = page.getContent();
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("genres", genres);
        return "choose-genres";
    }

    @GetMapping("/books/new-book/choose-genres/confirm")
    public String confirmGenresForBook(Model model, Integer[] genresIds) {
        List<GenreDto> genreDtoList = genreService.chooseGenres(genresIds);
        model.addAttribute("bookGenres", genreDtoList);
        return "add-book";
    }

    @GetMapping("/books/new-book/choose-publisher")
    public String choosePublisherForBook(Model model) {
        return showPaginatedSortedFilteredPublishers(1, 5, "id", "asc", null, model);
    }

    @GetMapping("/books/new-book/choose-publisher/page/{pageNumber}")
    public String showPaginatedSortedFilteredPublishers(@PathVariable("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize,
                                                    @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir,
                                                    @Param("nameFilter") String nameFilter, Model model) {
        PublisherFilter publisherFilter = PublisherFilter.builder()
                .nameFilter(nameFilter)
                .build();
        Page<PublisherDto> page = publisherService.findAllPublishersPaginatedSortedFiltered(publisherFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<PublisherDto> publishers = page.getContent();
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("publishers", publishers);
        return "choose-publisher";
    }

    @GetMapping("/books/new-book/choose-publisher/confirm")
    public String confirmPublisherForBook(Model model, int publisherId) {
        PublisherDto publisherDto = publisherService.findPublisherById(publisherId);
        model.addAttribute("bookPublisher", publisherDto);
        return "add-book";
    }

}
