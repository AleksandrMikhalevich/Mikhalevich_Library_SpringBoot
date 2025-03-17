package by.bsuir.iit.mikhalevich_library_springboot.service.mapper;

import by.bsuir.iit.mikhalevich_library_springboot.entity.Book;
import by.bsuir.iit.mikhalevich_library_springboot.service.dto.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
