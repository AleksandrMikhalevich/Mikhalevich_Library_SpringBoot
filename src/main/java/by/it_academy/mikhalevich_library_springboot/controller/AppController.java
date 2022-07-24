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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.sql.Date;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 18:00
 */
@Controller
@RequiredArgsConstructor
@SessionAttributes(value = {"bookAuthors", "bookGenres", "bookPublisher", "book", "author", "page", "size", "field", "dir"})
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
                null, null, null, null, null, null,
                null, null, null, model, sessionStatus);
    }

    @GetMapping("/books/page/{pageNumber}")
    public String showAllBooks(@PathVariable("pageNumber") int pageNumber, int pageSize,
                               String sortField, String sortDir, String titleFilter, String languageFilter,
                               String summaryFilter, String authorFilter, String genreFilter, String publisherFilter,
                               String fromDateFilter, String toDateFilter, String fromYearFilter, String toYearFilter,
                               Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        BookFilter bookFilter = BookFilter.builder()
                .titleFilter(titleFilter)
                .languageFilter(languageFilter)
                .summaryFilter(summaryFilter)
                .authorFilter(authorFilter)
                .genreFilter(genreFilter)
                .publisherFilter(publisherFilter)
                .receiptDateFromFilter(fromDateFilter)
                .receiptDateToFilter(toDateFilter)
                .yearOfPublishingFromFilter(fromYearFilter)
                .yearOfPublishingToFilter(toYearFilter)
                .build();
        Page<BookDto> page = bookService.findAllBooksPaginatedSortedFiltered(bookFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<BookDto> books = page.getContent();
        doPaginationSortingFiltration(pageNumber, pageSize, sortField, sortDir, model, totalPages, totalItems);
        doBookFilters(titleFilter, languageFilter, summaryFilter, authorFilter, genreFilter, publisherFilter, fromDateFilter,
                toDateFilter, fromYearFilter, toYearFilter, model);
        model.addAttribute("books", books);
        return "books";
    }


    @GetMapping("/books/new-book")
    public String newBook() {
        return "add-book";
    }

    @GetMapping("/books/new-book/choose-authors")
    public String chooseAuthorsForNewBook(Model model) {
        return showAuthorsForNewBook(1, 5, "id", "asc", null, null,
                null, null, null, null, model);
    }

    @GetMapping("/books/new-book/choose-authors/page/{pageNumber}")
    public String showAuthorsForNewBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                        String sortDir, String firstNameFilter,
                                        String secondNameFilter, String surnameFilter, String countryFilter, String bookFilter,
                                        String publisherFilter, Model model) {
        showAllAuthors(pageNumber, pageSize, sortField, sortDir, firstNameFilter, secondNameFilter, surnameFilter, countryFilter,
                bookFilter, publisherFilter, model);
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
        return showGenresForNewBook(1, 5, "id", "asc", null, null,
                null, model);
    }

    @GetMapping("/books/new-book/choose-genres/page/{pageNumber}")
    public String showGenresForNewBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                       String sortDir, String nameFilter, String descriptionFilter, String bookFilter,
                                       Model model) {
        showAllGenres(pageNumber, pageSize, sortField, sortDir, nameFilter, descriptionFilter, bookFilter, model);
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
        return showPublishersForNewBook(1, 5, "id", "asc", null, null,
                null,null,null,null, model);
    }

    @GetMapping("/books/new-book/choose-publisher/page/{pageNumber}")
    public String showPublishersForNewBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                           String sortDir, String nameFilter, String countryFilter, String cityFilter,
                                           String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        showAllPublishers(pageNumber, pageSize, sortField, sortDir, nameFilter, countryFilter, cityFilter, streetFilter,
                houseFilter, zipcodeFilter, model);
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
                             @PathVariable("sortDir") String sortDir, Model model, SessionStatus sessionStatus) {
        bookService.deleteBookById(id);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return showAllBooks(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, null, null,
                null, null, model, sessionStatus);
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
        return showAuthorsForEditBook(1, 5, "id", "asc", null, null,
                null, null, null, null, model);
    }

    @GetMapping("/books/edit-book/choose-authors/page/{pageNumber}")
    public String showAuthorsForEditBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                         String sortDir, String firstNameFilter, String secondNameFilter,
                                         String surnameFilter, String countryFilter, String bookFilter,
                                         String publisherFilter, Model model) {
        showAllAuthors(pageNumber, pageSize, sortField, sortDir, firstNameFilter, secondNameFilter, surnameFilter, countryFilter,
                bookFilter, publisherFilter, model);
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
        return showGenresForEditBook(1, 5, "id", "asc", null, null,
                null, model);
    }

    @GetMapping("/books/edit-book/choose-genres/page/{pageNumber}")
    public String showGenresForEditBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                        String sortDir, String nameFilter, String descriptionFilter, String bookFilter,
                                        Model model) {
        showAllGenres(pageNumber, pageSize, sortField, sortDir, nameFilter, descriptionFilter, bookFilter, model);
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
        return showPublishersForEditBook(1, 5, "id", "asc", null,null,
                null,null,null,null, model);
    }

    @GetMapping("/books/edit-book/choose-publisher/page/{pageNumber}")
    public String showPublishersForEditBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                            String sortDir, String nameFilter, String countryFilter, String cityFilter,
                                            String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        showAllPublishers(pageNumber, pageSize, sortField, sortDir, nameFilter, countryFilter, cityFilter, streetFilter,
                houseFilter, zipcodeFilter, model);
        return "edit-book-choose-publisher";
    }

    @GetMapping("/books/edit-book/choose-publisher/confirm")
    public String confirmPublisherForEditBook(Model model, int publisherId) {
        PublisherDto publisherDto = publisherService.findPublisherById(publisherId);
        model.addAttribute("bookPublisher", publisherDto);
        return "update-book";
    }

    @PostMapping("/books/update-book/{id}")
    public String updateBook(@PathVariable("id") int id, Integer[] authorsIds, Integer[] genresIds, Integer publisherId,
                             String title, String language, String summary, Date receiptDate, String yearOfPublishing,
                             int pageNumber, int pageSize, String sortField, String sortDir, Model model, SessionStatus sessionStatus) {
        bookService.updateBook(id, authorsIds, genresIds, publisherId, title, language, summary, receiptDate, yearOfPublishing);
        return showAllBooks(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, null, null,
                null, null, model, sessionStatus);
    }

    private void doBookFilters(String titleFilter, String languageFilter, String summaryFilter, String authorFilter,
                               String genreFilter, String publisherFilter, String fromDateFilter, String toDateFilter,
                               String fromYearFilter, String toYearFilter, Model model) {
        model.addAttribute("titleFilter", titleFilter);
        model.addAttribute("languageFilter", languageFilter);
        model.addAttribute("summaryFilter", summaryFilter);
        model.addAttribute("authorFilter", authorFilter);
        model.addAttribute("genreFilter", genreFilter);
        model.addAttribute("publisherFilter", publisherFilter);
        model.addAttribute("fromDateFilter", fromDateFilter);
        model.addAttribute("toDateFilter", toDateFilter);
        model.addAttribute("fromYearFilter", fromYearFilter);
        model.addAttribute("toYearFilter", toYearFilter);
    }

    private void showAllAuthors(int pageNumber, int pageSize, String sortField, String sortDir, String firstNameFilter,
                                String secondNameFilter, String surnameFilter, String countryFilter, String bookFilter,
                                String publisherFilter, Model model) {
        AuthorFilter authorFilter = AuthorFilter.builder()
                .firstNameFilter(firstNameFilter)
                .secondNameFilter(secondNameFilter)
                .surnameFilter(surnameFilter)
                .countryFilter(countryFilter)
                .bookFilter(bookFilter)
                .publisherFilter(publisherFilter)
                .build();
        Page<AuthorDto> page = authorService.findAllAuthorsPaginatedSortedFiltered(authorFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<AuthorDto> authors = page.getContent();
        doPaginationSortingFiltration(pageNumber, pageSize, sortField, sortDir, model, totalPages, totalItems);
        doAuthorFilters(firstNameFilter, secondNameFilter, surnameFilter, countryFilter, bookFilter, publisherFilter, model);
        model.addAttribute("authors", authors);
    }

    private void doAuthorFilters(String firstNameFilter, String secondNameFilter, String surnameFilter, String countryFilter,
                                 String bookFilter, String publisherFilter, Model model) {
        model.addAttribute("firstNameFilter", firstNameFilter);
        model.addAttribute("secondNameFilter", secondNameFilter);
        model.addAttribute("surnameFilter", surnameFilter);
        model.addAttribute("countryFilter", countryFilter);
        model.addAttribute("bookFilter", bookFilter);
        model.addAttribute("publisherFilter", publisherFilter);
    }

    private void showAllGenres(int pageNumber, int pageSize, String sortField, String sortDir, String nameFilter,
                               String descriptionFilter, String bookFilter, Model model) {
        GenreFilter genreFilter = GenreFilter.builder()
                .nameFilter(nameFilter)
                .descriptionFilter(descriptionFilter)
                .bookFilter(bookFilter)
                .build();
        Page<GenreDto> page = genreService.findAllGenresPaginatedSortedFiltered(genreFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<GenreDto> genres = page.getContent();
        doPaginationSortingFiltration(pageNumber, pageSize, sortField, sortDir, model, totalPages, totalItems);
        doGenreFilters(nameFilter, descriptionFilter, bookFilter, model);
        model.addAttribute("genres", genres);
    }

    private void doGenreFilters(String nameFilter, String descriptionFilter, String bookFilter, Model model) {
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("descriptionFilter", descriptionFilter);
        model.addAttribute("bookFilter", bookFilter);
    }

    private void showAllPublishers(int pageNumber, int pageSize, String sortField, String sortDir, String nameFilter,
                                   String countryFilter, String cityFilter, String streetFilter, String houseFilter,
                                   String zipcodeFilter, Model model) {
        PublisherFilter publisherFilter = PublisherFilter.builder()
                .nameFilter(nameFilter)
                .countryFilter(countryFilter)
                .cityFilter(cityFilter)
                .streetFilter(streetFilter)
                .houseFilter(houseFilter)
                .zipcodeFilter(zipcodeFilter)
                .build();
        Page<PublisherDto> page = publisherService.findAllPublishersPaginatedSortedFiltered(publisherFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<PublisherDto> publishers = page.getContent();
        doPaginationSortingFiltration(pageNumber, pageSize, sortField, sortDir, model, totalPages, totalItems);
        doPublisherFilters(nameFilter, countryFilter, cityFilter, streetFilter, houseFilter, zipcodeFilter, model);
        model.addAttribute("publishers", publishers);
    }

    private void doPublisherFilters(String nameFilter, String countryFilter, String cityFilter, String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("countryFilter", countryFilter);
        model.addAttribute("cityFilter", cityFilter);
        model.addAttribute("streetFilter", streetFilter);
        model.addAttribute("houseFilter", houseFilter);
        model.addAttribute("zipcodeFilter", zipcodeFilter);
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

    @GetMapping("/authors")
    public String showAuthorsFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        showAllAuthors(1, 5, "id", "asc", null, null, null,
                null, null, null, model);
        return "authors";
    }

    @GetMapping("/authors/page/{pageNumber}")
    public String showAuthors(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField, String sortDir, String firstNameFilter,
                              String secondNameFilter, String surnameFilter, String countryFilter, String bookFilter,
                              String publisherFilter, Model model) {
        showAllAuthors(pageNumber, pageSize, sortField, sortDir, firstNameFilter, secondNameFilter, surnameFilter, countryFilter,
                bookFilter, publisherFilter, model);
        return "authors";
    }

    @GetMapping("/authors/new-author")
    public String newAuthor() {
        return "add-author";
    }

    @GetMapping("/authors/new-author/choose-publishers")
    public String choosePublishersForNewAuthor(Model model) {
        return showPublishersForNewAuthor(1, 5, "id", "asc", null,null,
                null,null,null,null, model);
    }

    @GetMapping("/authors/new-author/choose-publishers/page/{pageNumber}")
    public String showPublishersForNewAuthor(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                             String sortDir, String nameFilter, String countryFilter, String cityFilter,
                                             String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        showAllPublishers(pageNumber, pageSize, sortField, sortDir, nameFilter, countryFilter, cityFilter, streetFilter,
                houseFilter, zipcodeFilter, model);
        return "new-author-choose-publishers";
    }

    @GetMapping("/authors/new-author/choose-publishers/confirm")
    public String confirmPublishersForNewAuthor(Model model, Integer[] publishersIds) {
        List<PublisherDto> publisherDtoList = publisherService.choosePublishers(publishersIds);
        model.addAttribute("authorPublishers", publisherDtoList);
        return "add-author";
    }

    @PostMapping("/authors/add-author")
    public String addAuthor(Integer[] publishersIds, String surname, String firstName, String secondName, String country) {
        authorService.addAuthor(publishersIds, surname, firstName, secondName, country);
        return "redirect:/authors";
    }

    @GetMapping("/authors/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editAuthor(@PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber,
                             @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                             @PathVariable("sortDir") String sortDir, Model model) {
        AuthorDto authorDto = authorService.findAuthorById(id);
        model.addAttribute("page", pageNumber);
        model.addAttribute("size", pageSize);
        model.addAttribute("field", sortField);
        model.addAttribute("dir", sortDir);
        model.addAttribute("author", authorDto);
        return "update-author";
    }

    @GetMapping("/authors/edit-author/choose-publishers")
    public String choosePublishersForEditAuthor(Model model) {
        return showPublishersForEditAuthor(1, 5, "id", "asc", null,null,
                null,null,null,null, model);
    }

    @GetMapping("/authors/edit-author/choose-publishers/page/{pageNumber}")
    public String showPublishersForEditAuthor(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                              String sortDir, String nameFilter, String countryFilter, String cityFilter,
                                              String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        showAllPublishers(pageNumber, pageSize, sortField, sortDir, nameFilter, countryFilter, cityFilter, streetFilter,
                houseFilter, zipcodeFilter, model);
        return "edit-author-choose-publishers";
    }

    @GetMapping("/authors/edit-author/choose-publishers/confirm")
    public String confirmPublishersForEditAuthor(Model model, Integer[] publishersIds) {
        List<PublisherDto> publisherDtoList = publisherService.choosePublishers(publishersIds);
        model.addAttribute("authorPublishers", publisherDtoList);
        return "update-author";
    }

    @PostMapping("/authors/update-author/{id}")
    public String updateAuthor(@PathVariable("id") int id, Integer[] publishersIds, String surname, String firstName,
                               String secondName, String country, int pageNumber, int pageSize, String sortField,
                               String sortDir, Model model) {
        authorService.updateAuthor(id, publishersIds, surname, firstName, secondName, country);
        return showAuthors(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, model);
    }

    @PostMapping("/authors/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteAuthor(@PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber,
                             @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                             @PathVariable("sortDir") String sortDir, Model model) {
        authorService.deleteAuthorById(id);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return showAuthors(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null,  model);
    }

    @GetMapping("/genres")
    public String showGenresFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        showAllGenres(1, 5, "id", "asc", null,
                null, null, model);
        return "genres";
    }

    @GetMapping("/genres/page/{pageNumber}")
    public String showGenres(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField, String sortDir,
                             String nameFilter, String descriptionFilter, String bookFilter, Model model) {
        showAllGenres(pageNumber, pageSize, sortField, sortDir, nameFilter, descriptionFilter, bookFilter, model);
        return "genres";
    }

    @GetMapping("/genres/new-genre")
    public String newGenre(@ModelAttribute("genre") GenreDto genreDto) {
        return "add-genre";
    }

    @PostMapping("/genres/add-genre")
    public String addGenre(@ModelAttribute("genre") @Valid GenreDto genreDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
           return "add-genre";
        genreService.addGenre(genreDto);
        return "redirect:/genres";
    }

    @PostMapping("/genres/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteGenre(@PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber,
                               @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                               @PathVariable("sortDir") String sortDir, Model model) {
        genreService.deleteGenreById(id);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return showGenres(pageNumber, pageSize, sortField, sortDir, null, null, null, model);
    }

    @GetMapping("/genres/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editGenre(@PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber,
                             @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                             @PathVariable("sortDir") String sortDir, Model model) {
        GenreDto genreDto = genreService.findGenreById(id);
        model.addAttribute("page", pageNumber);
        model.addAttribute("size", pageSize);
        model.addAttribute("field", sortField);
        model.addAttribute("dir", sortDir);
        model.addAttribute("genre", genreDto);
        return "update-genre";
    }

    @PostMapping("/genres/update-genre/{id}")
    public String updateGenre(@ModelAttribute("genre") @Valid GenreDto genreDto, BindingResult bindingResult, String nameFilter,
                              String descriptionFilter, String bookFilter, int pageNumber, int pageSize, String sortField,
                              String sortDir, Model model) {
        if (bindingResult.hasErrors())
            return "update-genre";
        genreService.updateGenre(genreDto);
        return showGenres(pageNumber, pageSize, sortField, sortDir, nameFilter, descriptionFilter, bookFilter, model);
    }

    @GetMapping("/publishers")
    public String showPublishersFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        showAllPublishers(1, 5, "id", "asc", null,null,null,
                null,null,null, model);
        return "publishers";
    }

    @GetMapping("/publishers/page/{pageNumber}")
    public String showPublishers(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                 String sortDir, String nameFilter, String countryFilter, String cityFilter,
                                 String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        showAllPublishers(pageNumber, pageSize, sortField, sortDir, nameFilter, countryFilter, cityFilter, streetFilter,
                houseFilter, zipcodeFilter, model);
        return "publishers";
    }

    @GetMapping("/publishers/new-publisher")
    public String newPublisher(@ModelAttribute("publisher") PublisherDto publisherDto) {
        return "add-publisher";
    }

    @PostMapping("/publishers/add-publisher")
    public String addPublisher(@ModelAttribute("publisher") @Valid PublisherDto publisherDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "add-publisher";
        publisherService.addPublisher(publisherDto);
        return "redirect:/publishers";
    }

    @PostMapping("/publishers/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deletePublisher(@PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber,
                              @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                              @PathVariable("sortDir") String sortDir, Model model) {
        publisherService.deletePublisherById(id);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return showPublishers(pageNumber, pageSize, sortField, sortDir, null, null, null,
                null,null,null, model);
    }

    @GetMapping("/publishers/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editPublisher(@PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber,
                            @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                            @PathVariable("sortDir") String sortDir, Model model) {
        PublisherDto publisherDto = publisherService.findPublisherById(id);
        model.addAttribute("page", pageNumber);
        model.addAttribute("size", pageSize);
        model.addAttribute("field", sortField);
        model.addAttribute("dir", sortDir);
        model.addAttribute("publisher", publisherDto);
        return "update-publisher";
    }

    @PostMapping("/publishers/update-publisher/{id}")
    public String updatePublisher(@ModelAttribute("publisher") @Valid PublisherDto publisherDto, BindingResult bindingResult, String nameFilter,
                              String countryFilter, String cityFilter, String streetFilter,String houseFilter, String zipcodeFilter,
                              int pageNumber, int pageSize, String sortField, String sortDir, Model model) {
        if (bindingResult.hasErrors())
            return "update-publisher";
        publisherService.updatePublisher(publisherDto);
        return showPublishers(pageNumber, pageSize, sortField, sortDir, nameFilter, countryFilter, cityFilter, streetFilter,
                houseFilter, zipcodeFilter, model);
    }

}
