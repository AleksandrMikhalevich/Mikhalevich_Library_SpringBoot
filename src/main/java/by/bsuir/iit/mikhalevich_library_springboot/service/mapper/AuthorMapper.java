package by.bsuir.iit.mikhalevich_library_springboot.service.mapper;

import by.bsuir.iit.mikhalevich_library_springboot.entity.Author;
import by.bsuir.iit.mikhalevich_library_springboot.service.dto.AuthorDto;
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
