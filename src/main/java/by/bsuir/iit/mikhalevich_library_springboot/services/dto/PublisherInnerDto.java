package by.bsuir.iit.mikhalevich_library_springboot.services.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author Alex Mikhalevich
 * @created 2024-09-01 21:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PublisherInnerDto extends PageSortDto {

    private String nameFilter;

    private String countryFilter;

    private String cityFilter;

    private String streetFilter;

    private String houseFilter;

    private String zipcodeFilter;

}
