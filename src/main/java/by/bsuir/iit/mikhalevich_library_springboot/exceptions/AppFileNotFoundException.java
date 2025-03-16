package by.bsuir.iit.mikhalevich_library_springboot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-21 15:05
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AppFileNotFoundException extends RuntimeException {

    public AppFileNotFoundException(String message) {
        super(message);
    }
}