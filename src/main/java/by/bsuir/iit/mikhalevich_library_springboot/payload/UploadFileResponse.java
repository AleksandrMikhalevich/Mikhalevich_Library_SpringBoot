package by.bsuir.iit.mikhalevich_library_springboot.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-21 14:59
 */
@Data
@AllArgsConstructor
public class UploadFileResponse {

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}
