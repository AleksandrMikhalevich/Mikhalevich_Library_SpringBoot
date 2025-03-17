package by.bsuir.iit.mikhalevich_library_springboot.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author Alex Mikhalevich
 * @created 2024-09-01 19:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserInnerDto extends PageSortDto {

    private String loginFilter;

    private String emailFilter;
}
