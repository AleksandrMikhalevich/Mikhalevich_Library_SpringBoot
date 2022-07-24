package by.it_academy.mikhalevich_library_springboot.services.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 15:22
 */
@Data
@Builder
public class AuthorDto implements Serializable {
    private final Integer id;
    private final String firstName;
    private final String secondName;
    private final String surname;
    private final String country;
    private final Set<BookDto> books;
    private final Set<PublisherDto> publishers;

    @Data
    public static class BookDto implements Serializable {
        private final Integer id;
        private final String title;
        private final String language;
        private final String summary;
        private final String yearOfPublishing;
        private final Date receiptDate;
    }

    @Data
    public static class PublisherDto implements Serializable {
        private final Integer id;
        private final String name;
        private final AddressDto address;
    }
}
