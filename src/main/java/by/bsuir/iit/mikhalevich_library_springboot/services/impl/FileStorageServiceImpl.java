package by.bsuir.iit.mikhalevich_library_springboot.services.impl;

import by.bsuir.iit.mikhalevich_library_springboot.entities.Book;
import by.bsuir.iit.mikhalevich_library_springboot.entities.DBFile;
import by.bsuir.iit.mikhalevich_library_springboot.exceptions.AppFileNotFoundException;
import by.bsuir.iit.mikhalevich_library_springboot.exceptions.FileStorageException;
import by.bsuir.iit.mikhalevich_library_springboot.repositories.BookRepository;
import by.bsuir.iit.mikhalevich_library_springboot.repositories.FileRepository;
import by.bsuir.iit.mikhalevich_library_springboot.services.interfaces.FileStorageService;
import by.bsuir.iit.mikhalevich_library_springboot.services.mappers.DBFileMapper;
import by.bsuir.iit.mikhalevich_library_springboot.services.dto.DBFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-21 15:00
 */
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileRepository fileRepository;
    private final BookRepository bookRepository;
    private final DBFileMapper fileMapper;

    @Override
    @Transactional
    public DBFileDto storeFile(Integer bookId, MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Имя файла " + fileName + "содержит неправильную последовательность символов");
            }
            Book book = bookRepository.findById(bookId).orElse(null);
            DBFile dbFile = DBFile.builder()
                    .id(bookId)
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .data(file.getBytes())
                    .book(book)
                    .build();
            assert book != null;
            book.setDbFile(dbFile);
            bookRepository.save(book);
            fileRepository.save(dbFile);
            return fileMapper.DBFileToDBFileDto(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Невозможно сохранить " + fileName + ex);
        }
    }

    @Override
    public DBFileDto findFileById(Integer id) {
        return fileRepository.findById(id).map(fileMapper::DBFileToDBFileDto)
                .orElseThrow(() -> new AppFileNotFoundException("Файл c " + id + "не найден"));
    }
}
