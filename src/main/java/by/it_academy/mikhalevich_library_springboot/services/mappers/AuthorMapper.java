package by.it_academy.mikhalevich_library_springboot.services.mappers;

import by.it_academy.mikhalevich_library_springboot.entities.Author;
import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorDto;
import org.mapstruct.Mapper;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 13:29
 */
@Mapper(componentModel = "spring")
public interface AuthorMapper {

    Author authorDtoToAuthor(AuthorDto authorDto);

    AuthorDto authorToAuthorDto(Author author);

}
