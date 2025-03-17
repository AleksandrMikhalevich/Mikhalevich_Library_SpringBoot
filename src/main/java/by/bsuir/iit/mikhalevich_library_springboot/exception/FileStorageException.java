package by.bsuir.iit.mikhalevich_library_springboot.exception;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-21 15:05
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }
}