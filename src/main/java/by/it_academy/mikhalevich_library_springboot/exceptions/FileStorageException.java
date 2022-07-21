package by.it_academy.mikhalevich_library_springboot.exceptions;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-21 15:05
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}