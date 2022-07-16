package by.it_academy.mikhalevich_library_springboot.filters;

import by.it_academy.mikhalevich_library_springboot.services.dto.AddressDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.PublisherDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 18:22
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublisherFilter {

    private String nameFilter;
//    TODO filters
    private AddressDto addressFilter;
    private Set<PublisherDto.BookDto> books;
    private Set<PublisherDto.AuthorDto> authors;
}
