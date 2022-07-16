package by.it_academy.mikhalevich_library_springboot.filters;

import by.it_academy.mikhalevich_library_springboot.services.dto.GenreDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 17:30
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreFilter {

    private String nameFilter;
//    TODO filters
    private String descriptionFilter;
    private Set<GenreDto.BookDto> books;
}
