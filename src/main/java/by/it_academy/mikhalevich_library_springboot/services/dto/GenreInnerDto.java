package by.it_academy.mikhalevich_library_springboot.services.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author Alex Mikhalevich
 * @created 2024-09-01 21:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class GenreInnerDto extends PageSortDto {

    private String nameFilter;

    private String descriptionFilter;

    private String bookFilter;
}
