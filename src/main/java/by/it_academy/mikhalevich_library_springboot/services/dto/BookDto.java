package by.it_academy.mikhalevich_library_springboot.services.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 15:36
 */
@Data
public class BookDto implements Serializable {
    private final Integer id;
    private final String title;
    private final String language;
    private final String summary;
    private final Set<AuthorDto> authors;
    private final Set<GenreDto> genres;
    private final PublisherDto publisher;
    private final String yearOfPublishing;
    private final Date receiptDate;

    @Data
    public static class AuthorDto implements Serializable {
        private final Integer id;
        private final String firstName;
        private final String secondName;
        private final String surname;
        private final String country;
    }

    @Data
    public static class GenreDto implements Serializable {
        private final Integer id;
        private final String name;
        private final String description;
    }

    @Data
    public static class PublisherDto implements Serializable {
        private final Integer id;
        private final String name;
        private final AddressDto address;
    }
}
