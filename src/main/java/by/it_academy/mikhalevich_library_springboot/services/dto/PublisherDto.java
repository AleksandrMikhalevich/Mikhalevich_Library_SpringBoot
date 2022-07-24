package by.it_academy.mikhalevich_library_springboot.services.dto;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-25 1:22
 */
@Data
public class PublisherDto implements Serializable {
    private final Integer id;
    @NotBlank(message = "Необходимо заполнить поле")
    private final String name;
    @NotBlank(message = "Необходимо заполнить поле")
    private final String addressCountry;
    @NotBlank(message = "Необходимо заполнить поле")
    private final String addressCity;
    @NotBlank(message = "Необходимо заполнить поле")
    private final String addressStreet;
    @Digits(message = "Необходимо заполнить поле, допустимы только числа", integer = 10, fraction = 0)
    private final String addressHouse;
    @NotBlank(message = "Необходимо заполнить поле")
    private final String addressZipcode;
    private final Set<BookDto> books;
    private final Set<AuthorDto> authors;

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
    public static class AuthorDto implements Serializable {
        private final Integer id;
        private final String firstName;
        private final String secondName;
        private final String surname;
        private final String country;
    }
}
