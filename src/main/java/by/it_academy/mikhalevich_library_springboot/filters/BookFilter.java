package by.it_academy.mikhalevich_library_springboot.filters;

import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

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
    private String authorFilter;
    private String genreFilter;
    private String publisherFilter;
    private Date receiptDate1Filter;
    private Date receiptDate2Filter;
    private String yearOfPublishingFilter;

}
