package by.it_academy.mikhalevich_library_springboot.services.mappers;

import by.it_academy.mikhalevich_library_springboot.entities.Genre;
import by.it_academy.mikhalevich_library_springboot.services.dto.GenreDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 13:40
 */
@Mapper(componentModel = "spring")
public interface GenreMapper {

    Genre genreDtoToGenre(GenreDto genreDto);

    GenreDto genreToGenreDto(Genre genre);

}
