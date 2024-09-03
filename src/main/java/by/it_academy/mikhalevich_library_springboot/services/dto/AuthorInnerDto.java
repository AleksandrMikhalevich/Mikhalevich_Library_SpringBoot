package by.it_academy.mikhalevich_library_springboot.services.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author Alex Mikhalevich
 * @created 2024-09-01 19:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class AuthorInnerDto extends PageSortDto {

    private String firstNameFilter;

    private String secondNameFilter;

    private String surnameFilter;

    private String countryFilter;

    private String bookFilter;

    private String publisherFilter;

}
