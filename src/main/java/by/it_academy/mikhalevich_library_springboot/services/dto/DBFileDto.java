package by.it_academy.mikhalevich_library_springboot.services.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-22 18:55
 */
@Data
@Builder
public class DBFileDto implements Serializable {
    private final Integer id;
    private final String fileName;
    private final String fileType;
    private final byte[] data;
    private final BookDto bookDto;
}
