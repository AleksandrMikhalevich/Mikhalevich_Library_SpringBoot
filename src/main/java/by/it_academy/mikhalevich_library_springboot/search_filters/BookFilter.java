package by.it_academy.mikhalevich_library_springboot.search_filters;

import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Set;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 16:14
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookFilter {

    private String titleFilter;
    private String languageFilter;
    private String summaryFilter;
    private BookDto.AuthorDto authorName;
    private BookDto.GenreDto genreName;
    private BookDto.PublisherDto publisherName;
    private String yearOfPublishing;
    private Date receiptDate;

}
