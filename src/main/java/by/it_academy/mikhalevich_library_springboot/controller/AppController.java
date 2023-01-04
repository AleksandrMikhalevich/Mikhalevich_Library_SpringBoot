package by.it_academy.mikhalevich_library_springboot.controller;

import by.it_academy.mikhalevich_library_springboot.filters.AuthorFilter;
import by.it_academy.mikhalevich_library_springboot.filters.BookFilter;
import by.it_academy.mikhalevich_library_springboot.filters.GenreFilter;
import by.it_academy.mikhalevich_library_springboot.filters.PublisherFilter;
import by.it_academy.mikhalevich_library_springboot.filters.UserFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.GenreDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.PublisherDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.UserDto;
import by.it_academy.mikhalevich_library_springboot.services.impl.UserDetailsServiceImpl;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.AuthorService;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.BookService;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.EmailService;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.GenreService;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Date;
import java.util.List;

import static by.it_academy.mikhalevich_library_springboot.constants.Constants.*;

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
    private final UserDetailsServiceImpl userDetailsService;
    private final EmailService emailService;

    @GetMapping(value = {"/", "/index"})
    public String showMain() {
        return "index";
    }

    @GetMapping("/users")
    public String showUsersFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        showAllUsers(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC, null,
                null, model);
        return "users";
    }

    @GetMapping("/users/page/{pageNumber}")
    public String showUsers(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField, String sortDir,
                            String loginFilter, String emailFilter, Model model) {
        showAllUsers(pageNumber, pageSize, sortField, sortDir, loginFilter, emailFilter, model);
        return "users";
    }

    @GetMapping("/registration")
    public String newUser(@ModelAttribute("user") UserDto user) {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("user") @Valid UserDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute(PASSWORD_ERROR, "Пароли не совпадают");
            return "registration";
        }
        if (!userDetailsService.saveUser(user)) {
            model.addAttribute(LOGIN_ERROR, "Пользователь с таким именем уже существует");
            return "registration";
        }
        String message = emailService.sendRegistrationMail(user.getEmail(), user.getLogin(), user.getPassword());
        model.addAttribute(REGISTRATION_MESSAGE, message);
        return "index";
    }

    @PostMapping("/users/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteUser(@PathVariable("id") Integer id, @PathVariable("pageNumber") int pageNumber,
                             @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                             @PathVariable("sortDir") String sortDir, Principal principal, Model model) {
        UserDto user = userDetailsService.findUserById(id);
        if (principal.getName().equals(user.getLogin())) {
            model.addAttribute(ACCOUNT_ERROR, "Удалить свой аккаунт вы можете через страницу профиля");
        } else {
            userDetailsService.deleteUserById(id);
        }
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(SORT_FIELD, sortField);
        model.addAttribute(SORT_DIR, sortDir);
        return showUsers(pageNumber, pageSize, sortField, sortDir, null, null, model);
    }

    @GetMapping("/user-account/{login}")
    public String editUser(@PathVariable("login") String login, Model model) {
        UserDto user = userDetailsService.findUserByLogin(login);
        model.addAttribute("user", user);
        return "user-account";
    }

    @PostMapping("/user-account/update-user/{id}")
    public String updateUser(@ModelAttribute("user") @Valid UserDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "user-account";
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute(PASSWORD_ERROR, "Пароли не совпадают");
            return "user-account";
        }
        userDetailsService.updateUser(user);
        String message = emailService.sendUpdateMail(user.getEmail(), user.getLogin(), user.getPassword());
        model.addAttribute(UPDATE_ACCOUNT_MESSAGE, message);
        return "index";
    }

    @PostMapping("/user-account/delete-user/{id}")
    public String deleteAccount(@PathVariable("id") Integer id) {
        userDetailsService.deleteUserById(id);
        return "redirect:/logout";
    }

    @GetMapping("/books")
    public String showBooksFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return showAllBooks(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null,
                null, null, null, null, model, sessionStatus);
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
        model.addAttribute(BOOKS, books);
        return "books";
    }

    @GetMapping("/books/new-book")
    public String newBook() {
        return "add-book";
    }

    @GetMapping("/books/new-book/choose-authors")
    public String chooseAuthorsForNewBook(Model model) {
        return showAuthorsForNewBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
    }

    @GetMapping("/books/new-book/choose-authors/page/{pageNumber}")
    public String showAuthorsForNewBook(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField,
                                        String sortDir, String firstNameFilter, String secondNameFilter, String surnameFilter,
                                        String countryFilter, String bookFilter, String publisherFilter, Model model) {
        showAllAuthors(pageNumber, pageSize, sortField, sortDir, firstNameFilter, secondNameFilter, surnameFilter, countryFilter,
                bookFilter, publisherFilter, model);
        return "new-book-choose-authors";
    }

    @GetMapping("/books/new-book/choose-authors/confirm")
    public String confirmAuthorsForNewBook(Model model, Integer[] authorsIds) {
        List<AuthorDto> authorDtoList = authorService.chooseAuthors(authorsIds);
        model.addAttribute(BOOK_AUTHORS, authorDtoList);
        return "add-book";
    }

    @GetMapping("/books/new-book/choose-genres")
    public String chooseGenresForNewBook(Model model) {
        return showGenresForNewBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, model);
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
        model.addAttribute(BOOK_GENRES, genreDtoList);
        return "add-book";
    }

    @GetMapping("/books/new-book/choose-publisher")
    public String choosePublisherForNewBook(Model model) {
        return showPublishersForNewBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
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
    public String confirmPublisherForNewBook(Model model, Integer publisherId) {
        PublisherDto publisherDto = publisherService.findPublisherById(publisherId);
        model.addAttribute(BOOK_PUBLISHER, publisherDto);
        return "add-book";
    }

    @PostMapping("/books/add-book")
    public String addBook(Integer[] authorsIds, Integer[] genresIds, Integer publisherId, String title, String language,
                          String summary, Date receiptDate, String yearOfPublishing) {
        bookService.addBook(authorsIds, genresIds, publisherId, title, language, summary, receiptDate, yearOfPublishing);
        return "redirect:/books";
    }

    @PostMapping("/books/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteBook(@PathVariable("id") Integer id, @PathVariable("pageNumber") int pageNumber,
                             @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                             @PathVariable("sortDir") String sortDir, Model model, SessionStatus sessionStatus) {
        bookService.deleteBookById(id);
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(SORT_FIELD, sortField);
        model.addAttribute(SORT_DIR, sortDir);
        return showAllBooks(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, null, null,
                null, null, model, sessionStatus);
    }

    @GetMapping("/books/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editBook(@PathVariable("id") Integer id, @PathVariable("pageNumber") int pageNumber,
                           @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                           @PathVariable("sortDir") String sortDir, Model model) {
        BookDto bookDto = bookService.findBookById(id);
        model.addAttribute(SESSION_PAGE, pageNumber);
        model.addAttribute(SESSION_SIZE, pageSize);
        model.addAttribute(SESSION_FIELD, sortField);
        model.addAttribute(SESSION_DIR, sortDir);
        model.addAttribute(SESSION_BOOK, bookDto);
        return "update-book";
    }

    @GetMapping("/books/edit-book/choose-authors")
    public String chooseAuthorsForEditBook(Model model) {
        return showAuthorsForEditBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
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
        model.addAttribute(BOOK_AUTHORS, authorDtoList);
        return "update-book";
    }

    @GetMapping("/books/edit-book/choose-genres")
    public String chooseGenresForEditBook(Model model) {
        return showGenresForEditBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, model);
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
        model.addAttribute(BOOK_GENRES, genreDtoList);
        return "update-book";
    }

    @GetMapping("/books/edit-book/choose-publisher")
    public String choosePublisherForEditBook(Model model) {
        return showPublishersForEditBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
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
    public String confirmPublisherForEditBook(Model model, Integer publisherId) {
        PublisherDto publisherDto = publisherService.findPublisherById(publisherId);
        model.addAttribute(BOOK_PUBLISHER, publisherDto);
        return "update-book";
    }

    @PostMapping("/books/update-book/{id}")
    public String updateBook(@PathVariable("id") Integer id, Integer[] authorsIds, Integer[] genresIds, Integer publisherId,
                             String title, String language, String summary, Date receiptDate, String yearOfPublishing,
                             int pageNumber, int pageSize, String sortField, String sortDir, Model model, SessionStatus sessionStatus) {
        bookService.updateBook(id, authorsIds, genresIds, publisherId, title, language, summary, receiptDate, yearOfPublishing);
        return showAllBooks(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, null, null,
                null, null, model, sessionStatus);
    }

    @GetMapping("/authors")
    public String showAuthorsFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        showAllAuthors(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC, null,
                null, null, null, null, null, model);
        return "authors";
    }

    @GetMapping("/authors/page/{pageNumber}")
    public String showAuthors(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField, String sortDir,
                              String firstNameFilter, String secondNameFilter, String surnameFilter, String countryFilter,
                              String bookFilter, String publisherFilter, Model model) {
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
        return showPublishersForNewAuthor(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
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
        model.addAttribute(AUTHOR_PUBLISHERS, publisherDtoList);
        return "add-author";
    }

    @PostMapping("/authors/add-author")
    public String addAuthor(Integer[] publishersIds, String surname, String firstName, String secondName, String country) {
        authorService.addAuthor(publishersIds, surname, firstName, secondName, country);
        return "redirect:/authors";
    }

    @GetMapping("/authors/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editAuthor(@PathVariable("id") Integer id, @PathVariable("pageNumber") int pageNumber,
                             @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                             @PathVariable("sortDir") String sortDir, Model model) {
        AuthorDto authorDto = authorService.findAuthorById(id);
        model.addAttribute(SESSION_PAGE, pageNumber);
        model.addAttribute(SESSION_SIZE, pageSize);
        model.addAttribute(SESSION_FIELD, sortField);
        model.addAttribute(SESSION_DIR, sortDir);
        model.addAttribute(SESSION_AUTHOR, authorDto);
        return "update-author";
    }

    @GetMapping("/authors/edit-author/choose-publishers")
    public String choosePublishersForEditAuthor(Model model) {
        return showPublishersForEditAuthor(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
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
    public String updateAuthor(@PathVariable("id") Integer id, Integer[] publishersIds, String surname, String firstName,
                               String secondName, String country, int pageNumber, int pageSize, String sortField,
                               String sortDir, Model model) {
        authorService.updateAuthor(id, publishersIds, surname, firstName, secondName, country);
        return showAuthors(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, model);
    }

    @PostMapping("/authors/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteAuthor(@PathVariable("id") Integer id, @PathVariable("pageNumber") int pageNumber,
                               @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                               @PathVariable("sortDir") String sortDir, Model model) {
        authorService.deleteAuthorById(id);
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(SORT_FIELD, sortField);
        model.addAttribute(SORT_DIR, sortDir);
        return showAuthors(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, model);
    }

    @GetMapping("/genres")
    public String showGenresFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        showAllGenres(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC, null,
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
    public String deleteGenre(@PathVariable("id") Integer id, @PathVariable("pageNumber") int pageNumber,
                              @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                              @PathVariable("sortDir") String sortDir, Model model) {
        genreService.deleteGenreById(id);
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(SORT_FIELD, sortField);
        model.addAttribute(SORT_DIR, sortDir);
        return showGenres(pageNumber, pageSize, sortField, sortDir, null, null, null, model);
    }

    @GetMapping("/genres/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editGenre(@PathVariable("id") Integer id, @PathVariable("pageNumber") int pageNumber,
                            @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                            @PathVariable("sortDir") String sortDir, Model model) {
        GenreDto genreDto = genreService.findGenreById(id);
        model.addAttribute(SESSION_PAGE, pageNumber);
        model.addAttribute(SESSION_SIZE, pageSize);
        model.addAttribute(SESSION_FIELD, sortField);
        model.addAttribute(SESSION_DIR, sortDir);
        model.addAttribute(GENRE, genreDto);
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
        showAllPublishers(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC, null,
                null, null, null, null, null, model);
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
    public String deletePublisher(@PathVariable("id") Integer id, @PathVariable("pageNumber") int pageNumber,
                                  @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                                  @PathVariable("sortDir") String sortDir, Model model) {
        publisherService.deletePublisherById(id);
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(SORT_FIELD, sortField);
        model.addAttribute(SORT_DIR, sortDir);
        return showPublishers(pageNumber, pageSize, sortField, sortDir, null, null, null,
                null, null, null, model);
    }

    @GetMapping("/publishers/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editPublisher(@PathVariable("id") Integer id, @PathVariable("pageNumber") int pageNumber,
                                @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                                @PathVariable("sortDir") String sortDir, Model model) {
        PublisherDto publisherDto = publisherService.findPublisherById(id);
        model.addAttribute(SESSION_PAGE, pageNumber);
        model.addAttribute(SESSION_SIZE, pageSize);
        model.addAttribute(SESSION_FIELD, sortField);
        model.addAttribute(SESSION_DIR, sortDir);
        model.addAttribute(PUBLISHER, publisherDto);
        return "update-publisher";
    }

    @PostMapping("/publishers/update-publisher/{id}")
    public String updatePublisher(@ModelAttribute("publisher") @Valid PublisherDto publisherDto, BindingResult bindingResult, String nameFilter,
                                  String countryFilter, String cityFilter, String streetFilter, String houseFilter, String zipcodeFilter,
                                  int pageNumber, int pageSize, String sortField, String sortDir, Model model) {
        if (bindingResult.hasErrors())
            return "update-publisher";
        publisherService.updatePublisher(publisherDto);
        return showPublishers(pageNumber, pageSize, sortField, sortDir, nameFilter, countryFilter, cityFilter, streetFilter,
                houseFilter, zipcodeFilter, model);
    }

    private void showAllUsers(int pageNumber, int pageSize, String sortField, String sortDir, String loginFilter,
                              String emailFilter, Model model) {
        UserFilter userFilter = UserFilter.builder()
                .loginFilter(loginFilter)
                .emailFilter(emailFilter)
                .build();
        Page<UserDto> page = userDetailsService.findAllUsersPaginatedSortedFiltered(userFilter, pageNumber, pageSize, sortField, sortDir);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<UserDto> users = page.getContent();
        doPaginationSortingFiltration(pageNumber, pageSize, sortField, sortDir, model, totalPages, totalItems);
        model.addAttribute("loginFilter", loginFilter);
        model.addAttribute("emailFilter", emailFilter);
        model.addAttribute("users", users);
    }

    private void doBookFilters(String titleFilter, String languageFilter, String summaryFilter, String authorFilter,
                               String genreFilter, String publisherFilter, String fromDateFilter, String toDateFilter,
                               String fromYearFilter, String toYearFilter, Model model) {
        model.addAttribute(BOOK_TITLE_FILTER, titleFilter);
        model.addAttribute(BOOK_LANGUAGE_FILTER, languageFilter);
        model.addAttribute(BOOK_SUMMARY_FILTER, summaryFilter);
        model.addAttribute(BOOK_AUTHOR_FILTER, authorFilter);
        model.addAttribute(BOOK_GENRE_FILTER, genreFilter);
        model.addAttribute(BOOK_PUBLISHER_FILTER, publisherFilter);
        model.addAttribute(BOOK_FROM_DATE_FILTER, fromDateFilter);
        model.addAttribute(BOOK_TO_DATE_FILTER, toDateFilter);
        model.addAttribute(BOOK_FROM_YEAR_FILTER, fromYearFilter);
        model.addAttribute(BOOK_TO_YEAR_FILTER, toYearFilter);
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
        model.addAttribute(AUTHORS, authors);
    }

    private void doAuthorFilters(String firstNameFilter, String secondNameFilter, String surnameFilter, String countryFilter,
                                 String bookFilter, String publisherFilter, Model model) {
        model.addAttribute(AUTHOR_FIRST_NAME_FILTER, firstNameFilter);
        model.addAttribute(AUTHOR_SECOND_NAME_FILTER, secondNameFilter);
        model.addAttribute(AUTHOR_SURNAME_FILTER, surnameFilter);
        model.addAttribute(AUTHOR_COUNTRY_FILTER, countryFilter);
        model.addAttribute(AUTHOR_BOOK_FILTER, bookFilter);
        model.addAttribute(AUTHOR_PUBLISHER_FILTER, publisherFilter);
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
        model.addAttribute(GENRES, genres);
    }

    private void doGenreFilters(String nameFilter, String descriptionFilter, String bookFilter, Model model) {
        model.addAttribute(GENRE_NAME_FILTER, nameFilter);
        model.addAttribute(GENRE_DESCRIPTION_FILTER, descriptionFilter);
        model.addAttribute(GENRE_BOOK_FILTER, bookFilter);
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
        model.addAttribute(PUBLISHERS, publishers);
    }

    private void doPublisherFilters(String nameFilter, String countryFilter, String cityFilter, String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        model.addAttribute(PUBLISHER_NAME_FILTER, nameFilter);
        model.addAttribute(PUBLISHER_COUNTRY_FILTER, countryFilter);
        model.addAttribute(PUBLISHER_CITY_FILTER, cityFilter);
        model.addAttribute(PUBLISHER_STREET_FILTER, streetFilter);
        model.addAttribute(PUBLISHER_HOUSE_FILTER, houseFilter);
        model.addAttribute(PUBLISHER_ZIPCODE_FILTER, zipcodeFilter);
    }

    private void doPaginationSortingFiltration(@PathVariable("pageNumber") int pageNumber, int pageSize, String sortField, String sortDir, Model model, int totalPages, long totalItems) {
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(TOTAL_PAGES, totalPages);
        model.addAttribute(TOTAL_ITEMS, totalItems);
        model.addAttribute(SORT_FIELD, sortField);
        model.addAttribute(SORT_DIR, sortDir);
        model.addAttribute(REVERSE_SORT_DIR, sortDir.equals(DEFAULT_SORT_DIR_ASC) ? SORT_DIR_DESC : DEFAULT_SORT_DIR_ASC);
    }

}
