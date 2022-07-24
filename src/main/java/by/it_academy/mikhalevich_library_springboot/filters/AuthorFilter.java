package by.it_academy.mikhalevich_library_springboot.filters;

import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-12 12:38
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorFilter {

    private String firstNameFilter;
    private String secondNameFilter;
    private String surnameFilter;
    private String countryFilter;
    private String bookFilter;
    private String publisherFilter;
}
