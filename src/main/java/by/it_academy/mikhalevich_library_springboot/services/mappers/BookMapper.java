package by.it_academy.mikhalevich_library_springboot.services.mappers;

import by.it_academy.mikhalevich_library_springboot.entities.Book;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import org.mapstruct.*;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 13:37
 */
@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "dbFile", source = "dbFileDto")
    Book bookDtoToBook(BookDto bookDto);

    @Mapping(target = "dbFileDto", source = "dbFile")
    BookDto bookToBookDto(Book book);

}
