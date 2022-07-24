package by.it_academy.mikhalevich_library_springboot.services.impl;

import by.it_academy.mikhalevich_library_springboot.entities.Genre;
import by.it_academy.mikhalevich_library_springboot.filters.GenreFilter;
import by.it_academy.mikhalevich_library_springboot.repositories.GenreRepository;
import by.it_academy.mikhalevich_library_springboot.services.dto.GenreDto;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.GenreService;
import by.it_academy.mikhalevich_library_springboot.services.mappers.GenreMapper;
import by.it_academy.mikhalevich_library_springboot.specifications.GenreSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 17:41
 */
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public Page<GenreDto> findAllGenresPaginatedSortedFiltered(GenreFilter genreFilter, int pageNumber, int pageSize, String sortField, String sortDirection) {
        Specification<Genre> genreSpecification =
                Specification
                        .where(Optional.ofNullable(genreFilter.getNameFilter())
                                .map(GenreSpecification::getGenreByNameSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(genreFilter.getDescriptionFilter())
                                .map(GenreSpecification::getGenreByDescriptionSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(genreFilter.getBookFilter())
                                .map(GenreSpecification::getGenreByBookTitleSpec)
                                .orElse(null));
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable paged = PageRequest.of(pageNumber - 1, pageSize, sort);
        return genreRepository.findAll(genreSpecification, paged).map(genreMapper::genreToGenreDto);
    }

    @Override
    public List<GenreDto> findAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::genreToGenreDto)
                .collect(Collectors.toList());
    }

    @Override
    public GenreDto findGenreById(int id) {
        return genreRepository.findById(id).map(genreMapper::genreToGenreDto).orElse(null);
    }

    @Override
    public void addGenre(GenreDto genreDto) {
        genreRepository.save(genreMapper.genreDtoToGenre(genreDto));
    }

    @Override
    public void updateGenre(GenreDto genreDto) {
        genreRepository.save(genreMapper.genreDtoToGenre(genreDto));
    }

    @Override
    public void deleteGenreById(int id) {
        genreRepository.deleteById(id);
    }

    @Override
    public List<GenreDto> chooseGenres(Integer[] genresIds) {
        List<Integer> listOfGenreIds = new ArrayList<>();
        Collections.addAll(listOfGenreIds, genresIds);
        return genreRepository.findAllById(listOfGenreIds).stream()
                .map(genreMapper::genreToGenreDto)
                .collect(Collectors.toList());
    }
}
