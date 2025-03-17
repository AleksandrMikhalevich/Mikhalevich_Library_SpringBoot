package by.bsuir.iit.mikhalevich_library_springboot.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-31 13:23
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilter {

    private String loginFilter;
    private String emailFilter;
}
