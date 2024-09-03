package by.it_academy.mikhalevich_library_springboot.services.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Alex Mikhalevich
 * @created 2024-09-01 19:19
 */
@Data
@SuperBuilder
public class PageSortDto implements Serializable {

    private Integer pageNumber;

    private Integer pageSize;

    private String sortField;

    private String sortDir;
}