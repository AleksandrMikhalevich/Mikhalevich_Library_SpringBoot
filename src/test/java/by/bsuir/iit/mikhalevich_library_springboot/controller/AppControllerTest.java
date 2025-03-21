package by.bsuir.iit.mikhalevich_library_springboot.controller;

import by.bsuir.iit.mikhalevich_library_springboot.filter.UserFilter;
import by.bsuir.iit.mikhalevich_library_springboot.service.dto.UserDto;
import by.bsuir.iit.mikhalevich_library_springboot.service.impl.UserDetailsServiceImpl;
import by.bsuir.iit.mikhalevich_library_springboot.service.interf.AuthorService;
import by.bsuir.iit.mikhalevich_library_springboot.service.interf.BookService;
import by.bsuir.iit.mikhalevich_library_springboot.service.interf.EmailService;
import by.bsuir.iit.mikhalevich_library_springboot.service.interf.GenreService;
import by.bsuir.iit.mikhalevich_library_springboot.service.interf.PublisherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Alex Mikhalevich
 * @created 2022-09-10 13:39
 */
@WebMvcTest(AppController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;
    @MockBean
    private AuthorService authorService;
    @MockBean
    private GenreService genreService;
    @MockBean
    private PublisherService publisherService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private EmailService emailService;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void shouldShowMainPage() throws Exception {
        mockMvc
                .perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void showUsersFirstPage() throws Exception {
        Page<UserDto> page = new PageImpl<>(List.of(new UserDto(1, "test_user", "test_user", "test_user@test.com", Set.of(new UserDto.RoleDto(1, "USER")))));
        List<UserDto> users = page.getContent();
        when(userDetailsService.findAllUsersPaginatedSortedFiltered(new UserFilter(), 1, 5, "id", "asc"))
                .thenReturn(page);
        mockMvc
                .perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attribute("users", users));
    }
}