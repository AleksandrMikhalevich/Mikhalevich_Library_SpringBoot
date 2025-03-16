package by.bsuir.iit.mikhalevich_library_springboot.services.interfaces;

import by.bsuir.iit.mikhalevich_library_springboot.filters.GenreFilter;
import by.bsuir.iit.mikhalevich_library_springboot.services.dto.GenreDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 17:28
 */
public interface GenreService {

    Page<GenreDto> findAllGenresPaginatedSortedFiltered(GenreFilter genreFilter, int pageNumber, int pageSize, String sortField, String sortDirection);

    GenreDto findGenreById(Integer id);

    void addGenre(GenreDto genreDto);

    void updateGenre(GenreDto genreDto);

    void deleteGenreById(Integer id);

    List<GenreDto> chooseGenres(Integer[] genresIds);
}
