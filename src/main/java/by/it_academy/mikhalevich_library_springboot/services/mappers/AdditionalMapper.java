package by.it_academy.mikhalevich_library_springboot.services.mappers;

import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.BookDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.GenreDto;
import by.it_academy.mikhalevich_library_springboot.services.dto.PublisherDto;
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

    BookDto.PublisherDto toBookPublisherDto(PublisherDto publisherDto);
}

