package by.bsuir.iit.mikhalevich_library_springboot.services.interfaces;

import by.bsuir.iit.mikhalevich_library_springboot.services.dto.DBFileDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-22 16:51
 */
public interface FileStorageService {

    DBFileDto storeFile(Integer bookId, MultipartFile file);

    DBFileDto findFileById(Integer id);
}
