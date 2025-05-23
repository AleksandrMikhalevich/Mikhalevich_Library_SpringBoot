package by.bsuir.iit.mikhalevich_library_springboot.service.impl;

import by.bsuir.iit.mikhalevich_library_springboot.entity.Genre;
import by.bsuir.iit.mikhalevich_library_springboot.filter.GenreFilter;
import by.bsuir.iit.mikhalevich_library_springboot.repository.GenreRepository;
import by.bsuir.iit.mikhalevich_library_springboot.service.interf.GenreService;
import by.bsuir.iit.mikhalevich_library_springboot.service.mapper.GenreMapper;
import by.bsuir.iit.mikhalevich_library_springboot.specification.GenreSpecification;
import by.bsuir.iit.mikhalevich_library_springboot.service.dto.GenreDto;
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
    public GenreDto findGenreById(Integer id) {
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
    public void deleteGenreById(Integer id) {
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
