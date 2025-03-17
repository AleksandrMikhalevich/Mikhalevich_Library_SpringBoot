package by.bsuir.iit.mikhalevich_library_springboot.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 17:30
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreFilter {

    private String nameFilter;
    private String descriptionFilter;
    private String bookFilter;
}
