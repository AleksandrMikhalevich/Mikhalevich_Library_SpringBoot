package by.it_academy.mikhalevich_library_springboot.controller;

import by.it_academy.mikhalevich_library_springboot.filters.AuthorFilter;
import by.it_academy.mikhalevich_library_springboot.filters.BookFilter;
import by.it_academy.mikhalevich_library_springboot.filters.GenreFilter;
import by.it_academy.mikhalevich_library_springboot.filters.PublisherFilter;
import by.it_academy.mikhalevich_library_springboot.filters.UserFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorInnerDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookInnerDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.GenreDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.GenreInnerDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.PublisherDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.PublisherInnerDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.UserDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.UserInnerDto;
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
        return INDEX_PAGE;
    }

    @GetMapping("/users")
    public String showUsersFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        UserInnerDto userInnerDto = UserInnerDto.builder()
                .pageNumber(DEFAULT_PAGE_NUMBER_1)
                .pageSize(DEFAULT_PAGE_SIZE_5)
                .sortField(DEFAULT_SORT_FIELD_ID)
                .sortDir(DEFAULT_SORT_DIR_ASC)
                .loginFilter(null)
                .emailFilter(null)
                .build();
        showAllUsers(userInnerDto, model);
        return USERS_PAGE;
    }

    @GetMapping("/users/page/{pageNumber}")
    public String showUsers(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize, String sortField,
                            String sortDir, String loginFilter, String emailFilter, Model model) {
        UserInnerDto userInnerDto = UserInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .loginFilter(loginFilter)
                .emailFilter(emailFilter)
                .build();
        showAllUsers(userInnerDto, model);
        return USERS_PAGE;
    }

    @GetMapping("/registration")
    public String newUser(@ModelAttribute("user") UserDto user) {
        return REGISTRATION_PAGE;
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("user") @Valid UserDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return REGISTRATION_PAGE;
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute(PASSWORD_ERROR, "Пароли не совпадают");
            return REGISTRATION_PAGE;
        }
        if (!userDetailsService.saveUser(user)) {
            model.addAttribute(LOGIN_ERROR, "Пользователь с таким именем уже существует");
            return REGISTRATION_PAGE;
        }
        String message = emailService.sendRegistrationMail(user.getEmail(), user.getLogin(), user.getPassword());
        model.addAttribute(REGISTRATION_MESSAGE, message);
        return INDEX_PAGE;
    }

    @PostMapping("/users/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteUser(@PathVariable("id") Integer id, @PathVariable("pageNumber") Integer pageNumber,
                             @PathVariable Integer pageSize, @PathVariable("sortField") String sortField,
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
        return USER_ACCOUNT_PAGE;
    }

    @PostMapping("/user-account/update-user/{id}")
    public String updateUser(@ModelAttribute("user") @Valid UserDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return USER_ACCOUNT_PAGE;
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute(PASSWORD_ERROR, "Пароли не совпадают");
            return USER_ACCOUNT_PAGE;
        }
        userDetailsService.updateUser(user);
        String message = emailService.sendUpdateMail(user.getEmail(), user.getLogin(), user.getPassword());
        model.addAttribute(UPDATE_ACCOUNT_MESSAGE, message);
        return INDEX_PAGE;
    }

    @PostMapping("/user-account/delete-user/{id}")
    public String deleteAccount(@PathVariable("id") Integer id) {
        userDetailsService.deleteUserById(id);
        return "redirect:/logout";
    }

    @GetMapping("/books")
    public String showBooksFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        BookInnerDto bookInnerDto = BookInnerDto.builder()
                .pageNumber(DEFAULT_PAGE_NUMBER_1)
                .pageSize(DEFAULT_PAGE_SIZE_5)
                .sortField(DEFAULT_SORT_FIELD_ID)
                .sortDir(DEFAULT_SORT_DIR_ASC)
                .titleFilter(null)
                .languageFilter(null)
                .summaryFilter(null)
                .authorFilter(null)
                .genreFilter(null)
                .publisherFilter(null)
                .fromDateFilter(null)
                .toDateFilter(null)
                .fromYearFilter(null)
                .toYearFilter(null)
                .build();
        showAllBooks(bookInnerDto, model);
        return BOOKS_PAGE;
    }

    @GetMapping("/books/page/{pageNumber}")
    public String showBooks(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize,
                               String sortField, String sortDir, String titleFilter, String languageFilter,
                               String summaryFilter, String authorFilter, String genreFilter, String publisherFilter,
                               String fromDateFilter, String toDateFilter, String fromYearFilter, String toYearFilter,
                               Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        BookInnerDto bookInnerDto = BookInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .titleFilter(titleFilter)
                .languageFilter(languageFilter)
                .summaryFilter(summaryFilter)
                .authorFilter(authorFilter)
                .genreFilter(genreFilter)
                .publisherFilter(publisherFilter)
                .fromDateFilter(fromDateFilter)
                .toDateFilter(toDateFilter)
                .fromYearFilter(fromYearFilter)
                .toYearFilter(toYearFilter)
                .build();
        showAllBooks(bookInnerDto, model);
        return BOOKS_PAGE;
    }

    @GetMapping("/books/new-book")
    public String newBook() {
        return ADD_BOOK_PAGE;
    }

    @GetMapping("/books/new-book/choose-authors")
    public String chooseAuthorsForNewBook(Model model) {
        return showAuthorsForNewBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
    }

    @GetMapping("/books/new-book/choose-authors/page/{pageNumber}")
    public String showAuthorsForNewBook(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize, String sortField,
                                        String sortDir, String firstNameFilter, String secondNameFilter, String surnameFilter,
                                        String countryFilter, String bookFilter, String publisherFilter, Model model) {
        AuthorInnerDto authorInnerDto = AuthorInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .firstNameFilter(firstNameFilter)
                .secondNameFilter(secondNameFilter)
                .surnameFilter(surnameFilter)
                .countryFilter(countryFilter)
                .bookFilter(bookFilter)
                .publisherFilter(publisherFilter)
                .build();
        showAllAuthors(authorInnerDto, model);
        return "new-book-choose-authors";
    }

    @GetMapping("/books/new-book/choose-authors/confirm")
    public String confirmAuthorsForNewBook(Model model, Integer[] authorsIds) {
        List<AuthorDto> authorDtoList = authorService.chooseAuthors(authorsIds);
        model.addAttribute(BOOK_AUTHORS, authorDtoList);
        return ADD_BOOK_PAGE;
    }

    @GetMapping("/books/new-book/choose-genres")
    public String chooseGenresForNewBook(Model model) {
        return showGenresForNewBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, model);
    }

    @GetMapping("/books/new-book/choose-genres/page/{pageNumber}")
    public String showGenresForNewBook(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize, String sortField,
                                       String sortDir, String nameFilter, String descriptionFilter, String bookFilter,
                                       Model model) {
        GenreInnerDto genreInnerDto = GenreInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .nameFilter(nameFilter)
                .descriptionFilter(descriptionFilter)
                .bookFilter(bookFilter)
                .build();
        showAllGenres(genreInnerDto, model);
        return "new-book-choose-genres";
    }

    @GetMapping("/books/new-book/choose-genres/confirm")
    public String confirmGenresForNewBook(Model model, Integer[] genresIds) {
        List<GenreDto> genreDtoList = genreService.chooseGenres(genresIds);
        model.addAttribute(BOOK_GENRES, genreDtoList);
        return ADD_BOOK_PAGE;
    }

    @GetMapping("/books/new-book/choose-publisher")
    public String choosePublisherForNewBook(Model model) {
        return showPublishersForNewBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
    }

    @GetMapping("/books/new-book/choose-publisher/page/{pageNumber}")
    public String showPublishersForNewBook(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize, String sortField,
                                           String sortDir, String nameFilter, String countryFilter, String cityFilter,
                                           String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        PublisherInnerDto publisherInnerDto = PublisherInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .nameFilter(nameFilter)
                .countryFilter(countryFilter)
                .cityFilter(cityFilter)
                .streetFilter(streetFilter)
                .houseFilter(houseFilter)
                .zipcodeFilter(zipcodeFilter)
                .build();
        showAllPublishers(publisherInnerDto, model);
        return "new-book-choose-publisher";
    }

    @GetMapping("/books/new-book/choose-publisher/confirm")
    public String confirmPublisherForNewBook(Model model, Integer publisherId) {
        PublisherDto publisherDto = publisherService.findPublisherById(publisherId);
        model.addAttribute(BOOK_PUBLISHER, publisherDto);
        return ADD_BOOK_PAGE;
    }

    @PostMapping("/books/add-book")
    public String addBook(Integer[] authorsIds, Integer[] genresIds, Integer publisherId, String title, String language,
                          String summary, Date receiptDate, String yearOfPublishing) {
        bookService.addBook(authorsIds, genresIds, publisherId, title, language, summary, receiptDate, yearOfPublishing);
        return "redirect:/books";
    }

    @PostMapping("/books/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteBook(@PathVariable("id") Integer id, @PathVariable("pageNumber") Integer pageNumber,
                             @PathVariable Integer pageSize, @PathVariable("sortField") String sortField,
                             @PathVariable("sortDir") String sortDir, Model model, SessionStatus sessionStatus) {
        bookService.deleteBookById(id);
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(SORT_FIELD, sortField);
        model.addAttribute(SORT_DIR, sortDir);
        BookInnerDto bookInnerDto = BookInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .titleFilter(null)
                .languageFilter(null)
                .summaryFilter(null)
                .authorFilter(null)
                .genreFilter(null)
                .publisherFilter(null)
                .fromDateFilter(null)
                .toDateFilter(null)
                .fromYearFilter(null)
                .toYearFilter(null)
                .build();
        sessionStatus.setComplete();
        showAllBooks(bookInnerDto, model);
        return BOOKS_PAGE;
    }

    @GetMapping("/books/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editBook(@PathVariable("id") Integer id, @PathVariable("pageNumber") Integer pageNumber,
                           @PathVariable Integer pageSize, @PathVariable("sortField") String sortField,
                           @PathVariable("sortDir") String sortDir, Model model) {
        BookDto bookDto = bookService.findBookById(id);
        model.addAttribute(SESSION_PAGE, pageNumber);
        model.addAttribute(SESSION_SIZE, pageSize);
        model.addAttribute(SESSION_FIELD, sortField);
        model.addAttribute(SESSION_DIR, sortDir);
        model.addAttribute(SESSION_BOOK, bookDto);
        return UPDATE_BOOK_PAGE;
    }

    @GetMapping("/books/edit-book/choose-authors")
    public String chooseAuthorsForEditBook(Model model) {
        return showAuthorsForEditBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
    }

    @GetMapping("/books/edit-book/choose-authors/page/{pageNumber}")
    public String showAuthorsForEditBook(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize,
                                         String sortField, String sortDir, String firstNameFilter,
                                         String secondNameFilter, String surnameFilter,
                                         String countryFilter, String bookFilter,
                                         String publisherFilter, Model model) {
        AuthorInnerDto authorInnerDto = AuthorInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .firstNameFilter(firstNameFilter)
                .secondNameFilter(secondNameFilter)
                .surnameFilter(surnameFilter)
                .countryFilter(countryFilter)
                .bookFilter(bookFilter)
                .publisherFilter(publisherFilter)
                .build();
        showAllAuthors(authorInnerDto, model);
        return "edit-book-choose-authors";
    }

    @GetMapping("/books/edit-book/choose-authors/confirm")
    public String confirmAuthorsForEditBook(Integer[] authorsIds, Model model) {
        List<AuthorDto> authorDtoList = authorService.chooseAuthors(authorsIds);
        model.addAttribute(BOOK_AUTHORS, authorDtoList);
        return UPDATE_BOOK_PAGE;
    }

    @GetMapping("/books/edit-book/choose-genres")
    public String chooseGenresForEditBook(Model model) {
        return showGenresForEditBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, model);
    }

    @GetMapping("/books/edit-book/choose-genres/page/{pageNumber}")
    public String showGenresForEditBook(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize, String sortField,
                                        String sortDir, String nameFilter, String descriptionFilter, String bookFilter,
                                        Model model) {
        GenreInnerDto genreInnerDto = GenreInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .nameFilter(nameFilter)
                .descriptionFilter(descriptionFilter)
                .bookFilter(bookFilter)
                .build();
        showAllGenres(genreInnerDto, model);
        return "edit-book-choose-genres";
    }

    @GetMapping("/books/edit-book/choose-genres/confirm")
    public String confirmGenresForEditBook(Model model, Integer[] genresIds) {
        List<GenreDto> genreDtoList = genreService.chooseGenres(genresIds);
        model.addAttribute(BOOK_GENRES, genreDtoList);
        return UPDATE_BOOK_PAGE;
    }

    @GetMapping("/books/edit-book/choose-publisher")
    public String choosePublisherForEditBook(Model model) {
        return showPublishersForEditBook(DEFAULT_PAGE_NUMBER_1, DEFAULT_PAGE_SIZE_5, DEFAULT_SORT_FIELD_ID, DEFAULT_SORT_DIR_ASC,
                null, null, null, null, null, null, model);
    }

    @GetMapping("/books/edit-book/choose-publisher/page/{pageNumber}")
    public String showPublishersForEditBook(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize, String sortField,
                                            String sortDir, String nameFilter, String countryFilter, String cityFilter,
                                            String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        PublisherInnerDto publisherInnerDto = PublisherInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .nameFilter(nameFilter)
                .countryFilter(countryFilter)
                .cityFilter(cityFilter)
                .streetFilter(streetFilter)
                .houseFilter(houseFilter)
                .zipcodeFilter(zipcodeFilter)
                .build();
        showAllPublishers(publisherInnerDto, model);
        return "edit-book-choose-publisher";
    }

    @GetMapping("/books/edit-book/choose-publisher/confirm")
    public String confirmPublisherForEditBook(Model model, Integer publisherId) {
        PublisherDto publisherDto = publisherService.findPublisherById(publisherId);
        model.addAttribute(BOOK_PUBLISHER, publisherDto);
        return UPDATE_BOOK_PAGE;
    }

    @PostMapping("/books/update-book/{id}")
    public String updateBook(@PathVariable("id") Integer id, Integer[] authorsIds, Integer[] genresIds, Integer publisherId,
                             String title, String language, String summary, Date receiptDate, String yearOfPublishing,
                             Integer pageNumber, Integer pageSize, String sortField, String sortDir, Model model,
                             SessionStatus sessionStatus) {
        bookService.updateBook(id, authorsIds, genresIds, publisherId, title, language, summary, receiptDate, yearOfPublishing);
        sessionStatus.setComplete();
        BookInnerDto bookInnerDto = BookInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .titleFilter(null)
                .languageFilter(null)
                .summaryFilter(null)
                .authorFilter(null)
                .genreFilter(null)
                .publisherFilter(null)
                .fromDateFilter(null)
                .toDateFilter(null)
                .fromYearFilter(null)
                .toYearFilter(null)
                .build();
        showAllBooks(bookInnerDto, model);
        return BOOKS_PAGE;
    }

    @GetMapping("/authors")
    public String showAuthorsFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        AuthorInnerDto authorInnerDto = AuthorInnerDto.builder()
                .pageNumber(DEFAULT_PAGE_NUMBER_1)
                .pageSize(DEFAULT_PAGE_SIZE_5)
                .sortField(DEFAULT_SORT_FIELD_ID)
                .sortDir(DEFAULT_SORT_DIR_ASC)
                .firstNameFilter(null)
                .secondNameFilter(null)
                .surnameFilter(null)
                .countryFilter(null)
                .bookFilter(null)
                .publisherFilter(null)
                .build();
        showAllAuthors(authorInnerDto, model);
        return "authors";
    }

    @GetMapping("/authors/page/{pageNumber}")
    public String showAuthors(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize, String sortField,
                              String sortDir, String firstNameFilter, String secondNameFilter, String surnameFilter,
                              String countryFilter, String bookFilter, String publisherFilter, Model model) {
        AuthorInnerDto authorInnerDto = AuthorInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .firstNameFilter(firstNameFilter)
                .secondNameFilter(secondNameFilter)
                .surnameFilter(surnameFilter)
                .countryFilter(countryFilter)
                .bookFilter(bookFilter)
                .publisherFilter(publisherFilter)
                .build();
        showAllAuthors(authorInnerDto, model);
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
    public String showPublishersForNewAuthor(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize,
                                             String sortField, String sortDir, String nameFilter, String countryFilter,
                                             String cityFilter, String streetFilter, String houseFilter,
                                             String zipcodeFilter, Model model) {
        PublisherInnerDto publisherInnerDto = PublisherInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .nameFilter(nameFilter)
                .countryFilter(countryFilter)
                .cityFilter(cityFilter)
                .streetFilter(streetFilter)
                .houseFilter(houseFilter)
                .zipcodeFilter(zipcodeFilter)
                .build();
        showAllPublishers(publisherInnerDto, model);
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
    public String editAuthor(@PathVariable("id") Integer id, @PathVariable("pageNumber") Integer pageNumber,
                             @PathVariable Integer pageSize, @PathVariable("sortField") String sortField,
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
    public String showPublishersForEditAuthor(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize,
                                              String sortField, String sortDir, String nameFilter, String countryFilter,
                                              String cityFilter, String streetFilter, String houseFilter,
                                              String zipcodeFilter, Model model) {
        PublisherInnerDto publisherInnerDto = PublisherInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .nameFilter(nameFilter)
                .countryFilter(countryFilter)
                .cityFilter(cityFilter)
                .streetFilter(streetFilter)
                .houseFilter(houseFilter)
                .zipcodeFilter(zipcodeFilter)
                .build();
        showAllPublishers(publisherInnerDto, model);
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
                               String secondName, String country, Integer pageNumber, Integer pageSize, String sortField,
                               String sortDir, Model model) {
        authorService.updateAuthor(id, publishersIds, surname, firstName, secondName, country);
        return showAuthors(pageNumber, pageSize, sortField, sortDir, null, null,
                null, null, null, null, model);
    }

    @PostMapping("/authors/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteAuthor(@PathVariable("id") Integer id, @PathVariable("pageNumber") Integer pageNumber,
                               @PathVariable Integer pageSize, @PathVariable("sortField") String sortField,
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
        GenreInnerDto genreInnerDto = GenreInnerDto.builder()
                .pageNumber(DEFAULT_PAGE_NUMBER_1)
                .pageSize(DEFAULT_PAGE_SIZE_5)
                .sortField(DEFAULT_SORT_FIELD_ID)
                .sortDir(DEFAULT_SORT_DIR_ASC)
                .nameFilter(null)
                .descriptionFilter(null)
                .bookFilter(null)
                .build();
        showAllGenres(genreInnerDto, model);
        return "genres";
    }

    @GetMapping("/genres/page/{pageNumber}")
    public String showGenres(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize, String sortField,
                             String sortDir, String nameFilter, String descriptionFilter, String bookFilter,
                             Model model) {
        GenreInnerDto genreInnerDto = GenreInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .nameFilter(nameFilter)
                .descriptionFilter(descriptionFilter)
                .bookFilter(bookFilter)
                .build();
        showAllGenres(genreInnerDto, model);
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
    public String deleteGenre(@PathVariable("id") Integer id, @PathVariable("pageNumber") Integer pageNumber,
                              @PathVariable Integer pageSize, @PathVariable("sortField") String sortField,
                              @PathVariable("sortDir") String sortDir, Model model) {
        genreService.deleteGenreById(id);
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(SORT_FIELD, sortField);
        model.addAttribute(SORT_DIR, sortDir);
        return showGenres(pageNumber, pageSize, sortField, sortDir, null, null, null, model);
    }

    @GetMapping("/genres/page/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String editGenre(@PathVariable("id") Integer id, @PathVariable("pageNumber") Integer pageNumber,
                            @PathVariable Integer pageSize, @PathVariable("sortField") String sortField,
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
    public String updateGenre(@ModelAttribute("genre") @Valid GenreDto genreDto, BindingResult bindingResult,
                              String nameFilter, String descriptionFilter, String bookFilter, Integer pageNumber,
                              Integer pageSize, String sortField, String sortDir, Model model) {
        if (bindingResult.hasErrors())
            return "update-genre";
        genreService.updateGenre(genreDto);
        return showGenres(pageNumber, pageSize, sortField, sortDir, nameFilter, descriptionFilter, bookFilter, model);
    }

    @GetMapping("/publishers")
    public String showPublishersFirstPage(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        PublisherInnerDto publisherInnerDto = PublisherInnerDto.builder()
                .pageNumber(DEFAULT_PAGE_NUMBER_1)
                .pageSize(DEFAULT_PAGE_SIZE_5)
                .sortField(DEFAULT_SORT_FIELD_ID)
                .sortDir(DEFAULT_SORT_DIR_ASC)
                .nameFilter(null)
                .countryFilter(null)
                .cityFilter(null)
                .streetFilter(null)
                .houseFilter(null)
                .zipcodeFilter(null)
                .build();
        showAllPublishers(publisherInnerDto, model);
        return "publishers";
    }

    @GetMapping("/publishers/page/{pageNumber}")
    public String showPublishers(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize, String sortField,
                                 String sortDir, String nameFilter, String countryFilter, String cityFilter,
                                 String streetFilter, String houseFilter, String zipcodeFilter, Model model) {
        PublisherInnerDto publisherInnerDto = PublisherInnerDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortField(sortField)
                .sortDir(sortDir)
                .nameFilter(nameFilter)
                .countryFilter(countryFilter)
                .cityFilter(cityFilter)
                .streetFilter(streetFilter)
                .houseFilter(houseFilter)
                .zipcodeFilter(zipcodeFilter)
                .build();
        showAllPublishers(publisherInnerDto, model);
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
    public String deletePublisher(@PathVariable("id") Integer id, @PathVariable("pageNumber") Integer pageNumber,
                                  @PathVariable Integer pageSize, @PathVariable("sortField") String sortField,
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
    public String editPublisher(@PathVariable("id") Integer id, @PathVariable("pageNumber") Integer pageNumber,
                                @PathVariable Integer pageSize, @PathVariable("sortField") String sortField,
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
    public String updatePublisher(@ModelAttribute("publisher") @Valid PublisherDto publisherDto, BindingResult bindingResult,
                                  String nameFilter, String countryFilter, String cityFilter, String streetFilter,
                                  String houseFilter, String zipcodeFilter, Integer pageNumber, Integer pageSize,
                                  String sortField, String sortDir, Model model) {
        if (bindingResult.hasErrors()) {
            return "update-publisher";
        }
        publisherService.updatePublisher(publisherDto);
        return showPublishers(pageNumber, pageSize, sortField, sortDir, nameFilter, countryFilter, cityFilter,
                streetFilter, houseFilter, zipcodeFilter, model);
    }

    private void showAllUsers(UserInnerDto dto, Model model) {
        UserFilter userFilter = UserFilter.builder()
                .loginFilter(dto.getLoginFilter())
                .emailFilter(dto.getLoginFilter())
                .build();
        Page<UserDto> page = userDetailsService.findAllUsersPaginatedSortedFiltered(userFilter, dto.getPageNumber(),
                dto.getPageSize(), dto.getSortField(), dto.getSortDir());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<UserDto> users = page.getContent();
        doPaginationSortingFiltration(dto.getPageNumber(), dto.getPageSize(), dto.getSortField(), dto.getSortDir(),
                model, totalPages, totalItems);
        model.addAttribute("loginFilter", dto.getLoginFilter());
        model.addAttribute("emailFilter", dto.getEmailFilter());
        model.addAttribute(USERS_PAGE, users);
    }

    private void showAllBooks(BookInnerDto dto, Model model) {
        BookFilter bookFilter = BookFilter.builder()
                .titleFilter(dto.getTitleFilter())
                .languageFilter(dto.getLanguageFilter())
                .summaryFilter(dto.getSummaryFilter())
                .authorFilter(dto.getAuthorFilter())
                .genreFilter(dto.getGenreFilter())
                .publisherFilter(dto.getPublisherFilter())
                .receiptDateFromFilter(dto.getFromDateFilter())
                .receiptDateToFilter(dto.getToDateFilter())
                .yearOfPublishingFromFilter(dto.getFromYearFilter())
                .yearOfPublishingToFilter(dto.getToYearFilter())
                .build();
        Page<BookDto> page = bookService.findAllBooksPaginatedSortedFiltered(bookFilter, dto.getPageNumber(),
                dto.getPageSize(), dto.getSortField(), dto.getSortDir());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<BookDto> books = page.getContent();
        doPaginationSortingFiltration(dto.getPageNumber(), dto.getPageSize(), dto.getSortField(), dto.getSortDir(),
                model, totalPages, totalItems);
        doBookFilters(bookFilter, model);
        model.addAttribute(BOOKS, books);
    }

    private void doBookFilters(BookFilter filter, Model model) {
        model.addAttribute(BOOK_TITLE_FILTER, filter.getTitleFilter());
        model.addAttribute(BOOK_LANGUAGE_FILTER, filter.getLanguageFilter());
        model.addAttribute(BOOK_SUMMARY_FILTER, filter.getSummaryFilter());
        model.addAttribute(BOOK_AUTHOR_FILTER, filter.getAuthorFilter());
        model.addAttribute(BOOK_GENRE_FILTER, filter.getGenreFilter());
        model.addAttribute(BOOK_PUBLISHER_FILTER, filter.getPublisherFilter());
        model.addAttribute(BOOK_FROM_DATE_FILTER, filter.getReceiptDateFromFilter());
        model.addAttribute(BOOK_TO_DATE_FILTER, filter.getReceiptDateToFilter());
        model.addAttribute(BOOK_FROM_YEAR_FILTER, filter.getYearOfPublishingFromFilter());
        model.addAttribute(BOOK_TO_YEAR_FILTER, filter.getYearOfPublishingToFilter());
    }

    private void showAllAuthors(AuthorInnerDto dto, Model model) {
        AuthorFilter authorFilter = AuthorFilter.builder()
                .firstNameFilter(dto.getFirstNameFilter())
                .secondNameFilter(dto.getSecondNameFilter())
                .surnameFilter(dto.getSurnameFilter())
                .countryFilter(dto.getCountryFilter())
                .bookFilter(dto.getBookFilter())
                .publisherFilter(dto.getPublisherFilter())
                .build();
        Page<AuthorDto> page = authorService.findAllAuthorsPaginatedSortedFiltered(authorFilter, dto.getPageNumber(),
                dto.getPageSize(), dto.getSortField(), dto.getSortDir());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<AuthorDto> authors = page.getContent();
        doPaginationSortingFiltration(dto.getPageNumber(), dto.getPageSize(), dto.getSortField(), dto.getSortDir(),
                model, totalPages, totalItems);
        doAuthorFilters(authorFilter, model);
        model.addAttribute(AUTHORS, authors);
    }

    private void doAuthorFilters(AuthorFilter filter, Model model) {
        model.addAttribute(AUTHOR_FIRST_NAME_FILTER, filter.getFirstNameFilter());
        model.addAttribute(AUTHOR_SECOND_NAME_FILTER, filter.getSecondNameFilter());
        model.addAttribute(AUTHOR_SURNAME_FILTER, filter.getSurnameFilter());
        model.addAttribute(AUTHOR_COUNTRY_FILTER, filter.getCountryFilter());
        model.addAttribute(AUTHOR_BOOK_FILTER, filter.getBookFilter());
        model.addAttribute(AUTHOR_PUBLISHER_FILTER, filter.getPublisherFilter());
    }

    private void showAllGenres(GenreInnerDto dto, Model model) {
        GenreFilter genreFilter = GenreFilter.builder()
                .nameFilter(dto.getNameFilter())
                .descriptionFilter(dto.getDescriptionFilter())
                .bookFilter(dto.getBookFilter())
                .build();
        Page<GenreDto> page = genreService.findAllGenresPaginatedSortedFiltered(genreFilter, dto.getPageNumber(),
                dto.getPageSize(), dto.getSortField(), dto.getSortDir());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<GenreDto> genres = page.getContent();
        doPaginationSortingFiltration(dto.getPageNumber(), dto.getPageSize(), dto.getSortField(), dto.getSortDir(),
                model, totalPages, totalItems);
        doGenreFilters(genreFilter, model);
        model.addAttribute(GENRES, genres);
    }

    private void doGenreFilters(GenreFilter filter, Model model) {
        model.addAttribute(GENRE_NAME_FILTER, filter.getNameFilter());
        model.addAttribute(GENRE_DESCRIPTION_FILTER, filter.getDescriptionFilter());
        model.addAttribute(GENRE_BOOK_FILTER, filter.getBookFilter());
    }

    private void showAllPublishers(PublisherInnerDto dto, Model model) {
        PublisherFilter publisherFilter = PublisherFilter.builder()
                .nameFilter(dto.getNameFilter())
                .countryFilter(dto.getCountryFilter())
                .cityFilter(dto.getCityFilter())
                .streetFilter(dto.getStreetFilter())
                .houseFilter(dto.getHouseFilter())
                .zipcodeFilter(dto.getZipcodeFilter())
                .build();
        Page<PublisherDto> page = publisherService.findAllPublishersPaginatedSortedFiltered(publisherFilter,
                dto.getPageNumber(), dto.getPageSize(), dto.getSortField(), dto.getSortDir());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<PublisherDto> publishers = page.getContent();
        doPaginationSortingFiltration(dto.getPageNumber(), dto.getPageSize(), dto.getSortField(), dto.getSortDir(),
                model, totalPages, totalItems);
        doPublisherFilters(publisherFilter, model);
        model.addAttribute(PUBLISHERS, publishers);
    }

    private void doPublisherFilters(PublisherFilter filter, Model model) {
        model.addAttribute(PUBLISHER_NAME_FILTER, filter.getNameFilter());
        model.addAttribute(PUBLISHER_COUNTRY_FILTER, filter.getCountryFilter());
        model.addAttribute(PUBLISHER_CITY_FILTER, filter.getCityFilter());
        model.addAttribute(PUBLISHER_STREET_FILTER, filter.getStreetFilter());
        model.addAttribute(PUBLISHER_HOUSE_FILTER, filter.getHouseFilter());
        model.addAttribute(PUBLISHER_ZIPCODE_FILTER, filter.getZipcodeFilter());
    }

    private void doPaginationSortingFiltration(@PathVariable("pageNumber") Integer pageNumber, Integer pageSize,
                                               String sortField, String sortDir, Model model, int totalPages,
                                               long totalItems) {
        model.addAttribute(CURRENT_PAGE, pageNumber);
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(TOTAL_PAGES, totalPages);
        model.addAttribute(TOTAL_ITEMS, totalItems);
        model.addAttribute(SORT_FIELD, sortField);
        model.addAttribute(SORT_DIR, sortDir);
        model.addAttribute(REVERSE_SORT_DIR, sortDir.equals(DEFAULT_SORT_DIR_ASC) ? SORT_DIR_DESC : DEFAULT_SORT_DIR_ASC);
    }

}
