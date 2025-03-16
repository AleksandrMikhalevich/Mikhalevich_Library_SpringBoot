package by.bsuir.iit.mikhalevich_library_springboot.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String receiptDateFromFilter;
    private String receiptDateToFilter;
    private String yearOfPublishingFromFilter;
    private String yearOfPublishingToFilter;

}
