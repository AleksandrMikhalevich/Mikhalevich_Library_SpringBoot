package by.bsuir.iit.mikhalevich_library_springboot.services.mappers;

import by.bsuir.iit.mikhalevich_library_springboot.services.dto.BookDto;
import by.bsuir.iit.mikhalevich_library_springboot.services.dto.AuthorDto;
import by.bsuir.iit.mikhalevich_library_springboot.services.dto.GenreDto;
import by.bsuir.iit.mikhalevich_library_springboot.services.dto.PublisherDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-13 18:46
 */
@Mapper(componentModel = "spring")
public interface AdditionalMapper {

    BookDto.AuthorDto toBookAuthorDto(AuthorDto authorDto);

    BookDto.GenreDto toBookGenreDto(GenreDto genreDto);

    @Mapping(target = "address", source = ".")
    BookDto.PublisherDto toBookPublisherDto(PublisherDto publisherDto);

    @Mapping(target = "address", source = ".")
    AuthorDto.PublisherDto toAuthorPublisherDto(PublisherDto publisherDto);
}

