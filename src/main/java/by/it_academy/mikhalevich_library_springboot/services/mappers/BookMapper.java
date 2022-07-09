package by.it_academy.mikhalevich_library_springboot.services.mappers;

import by.it_academy.mikhalevich_library_springboot.entities.Book;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 13:37
 */
@Mapper(componentModel = "spring")
public interface BookMapper {

    Book bookDtoToBook(BookDto bookDto);

    BookDto bookToBookDto(Book book);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Book updateBookFromBookDto(BookDto bookDto, @MappingTarget Book book);
}
