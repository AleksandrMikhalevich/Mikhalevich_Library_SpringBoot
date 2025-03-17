package by.bsuir.iit.mikhalevich_library_springboot.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 18:22
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublisherFilter {

    private String nameFilter;
    private String countryFilter;
    private String cityFilter;
    private String streetFilter;
    private String houseFilter;
    private String zipcodeFilter;
}
