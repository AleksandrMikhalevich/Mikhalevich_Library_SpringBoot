package by.it_academy.mikhalevich_library_springboot.services.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 15:30
 */
@Data
public class GenreDto implements Serializable {
    private final Integer id;
    private final String name;
    private final String description;
    private final Set<BookDto> books;

    @Data
    public static class BookDto implements Serializable {
        private final Integer id;
        private final String title;
        private final String language;
        private final String summary;
        private final String yearOfPublishing;
        private final Date receiptDate;
    }
}
