package by.it_academy.mikhalevich_library_springboot.services.interfaces;

import by.it_academy.mikhalevich_library_springboot.filters.GenreFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.GenreDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 17:28
 */
public interface GenreService {

    Page<GenreDto> findAllGenresPaginatedSortedFiltered(GenreFilter genreFilter, int pageNumber, int pageSize, String sortField, String sortDirection);

    List<GenreDto> findAllGenres();

    GenreDto findGenreById(int id);

    void addGenre(GenreDto genreDto);

    void updateGenre(GenreDto genreDto);

    void deleteGenreById(int id);

    List<GenreDto> chooseGenres(Integer[] genresIds);
}
