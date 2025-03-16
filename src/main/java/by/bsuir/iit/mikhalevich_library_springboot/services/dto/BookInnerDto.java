package by.bsuir.iit.mikhalevich_library_springboot.services.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author Alex Mikhalevich
 * @created 2024-09-01 21:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class BookInnerDto extends PageSortDto {

    private String titleFilter;

    private String languageFilter;

    private String summaryFilter;

    private String authorFilter;

    private String genreFilter;

    private String publisherFilter;

    private String fromDateFilter;

    private String toDateFilter;

    private String fromYearFilter;

    private String toYearFilter;

}
