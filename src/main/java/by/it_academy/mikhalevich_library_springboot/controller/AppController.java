package by.it_academy.mikhalevich_library_springboot.controller;

import by.it_academy.mikhalevich_library_springboot.search_filters.BookFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 18:00
 */
@Controller
@RequiredArgsConstructor
public class AppController {

    private final BookService bookService;

    @GetMapping("/")
    public String showMain(Model model) {
        return "index";
    }

    @GetMapping("/new-book")
    public String newBook(@ModelAttribute("book") BookDto bookDto) {
        return "add-book";
    }

    @PostMapping()
    public String addBook(@ModelAttribute("book") BookDto bookDto) {
        bookService.addBook(bookDto);
        return "redirect:/";
    }

    @GetMapping("/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
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

    @PatchMapping("/{id}")
    public String updateBook(Model model, @ModelAttribute("book") @Valid BookDto bookDto, int pageNumber, int pageSize, String sortField, String sortDir,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "update-book";
        bookService.updateBook(bookDto);
        return showPaginatedSortedFilteredBooks(pageNumber, pageSize, sortField, sortDir, null, model);
    }

    @DeleteMapping("/{id}/{pageNumber}/{pageSize}/{sortField}/{sortDir}")
    public String deleteBook(Model model, @PathVariable("id") int id, @PathVariable("pageNumber") int pageNumber, @PathVariable int pageSize, @PathVariable("sortField") String sortField,
                              @PathVariable("sortDir") String sortDir) {
        bookService.deleteBookById(id);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return showPaginatedSortedFilteredBooks(pageNumber, pageSize, sortField, sortDir, null, model);
    }

    @GetMapping("/page/{pageNumber}")
    public String showPaginatedSortedFilteredBooks(@PathVariable("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize,
                                                   @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir,
                                                   @Param("titleFilter") String titleFilter, Model model) {
        BookFilter bookFilter = BookFilter.builder()
                .titleFilter(titleFilter)
                .build();
        Page<BookDto> page = bookService.findAllPaginatedSortedFiltered(bookFilter, pageNumber, pageSize, sortField, sortDir);
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

    @GetMapping("/books")
    public String showBooksFirstPage(Model model) {
        return showPaginatedSortedFilteredBooks(1, 5, "id", "asc", null, model);
    }
}
