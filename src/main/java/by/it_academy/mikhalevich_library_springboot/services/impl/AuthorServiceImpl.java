package by.it_academy.mikhalevich_library_springboot.services.impl;

import by.it_academy.mikhalevich_library_springboot.entities.Author;
import by.it_academy.mikhalevich_library_springboot.repositories.AuthorRepository;
import by.it_academy.mikhalevich_library_springboot.filters.AuthorFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorDto;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.AuthorService;
import by.it_academy.mikhalevich_library_springboot.services.mappers.AuthorMapper;
import by.it_academy.mikhalevich_library_springboot.specifications.AuthorSpecification;
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
 * @created 2022-07-12 12:25
 */
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public Page<AuthorDto> findAllAuthorsPaginatedSortedFiltered(AuthorFilter authorFilter, int pageNumber, int pageSize, String sortField, String sortDirection) {
        Specification<Author> authorSpecification =
                Specification
                        .where(Optional.ofNullable(authorFilter.getFirstNameFilter())
                                .map(AuthorSpecification::getAuthorByFirstNameSpec)
                                .orElse(null));
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable paged = PageRequest.of(pageNumber - 1, pageSize, sort);
        return authorRepository.findAll(authorSpecification, paged).map(authorMapper::authorToAuthorDto);
    }

    @Override
    public List<AuthorDto> findAllAuthors() {
        return authorRepository.findAll().stream()
                .map(authorMapper ::authorToAuthorDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorDto findAuthorById(int id) {
        return authorRepository.findById(id).map(authorMapper::authorToAuthorDto).orElse(null);
    }

    @Override
    public void addAuthor(AuthorDto authorDto) {
        authorRepository.save(authorMapper.authorDtoToAuthor(authorDto));
    }

    @Override
    public void updateAuthor(AuthorDto authorDto) {
        authorRepository.save(authorMapper.authorDtoToAuthor(authorDto));
    }

    @Override
    public void deleteAuthorById(int id) {
        authorRepository.deleteById(id);
    }

    @Override
    public List<AuthorDto> chooseAuthors(Integer[] authorsIds) {
        List<Integer> listOfAuthorIds = new ArrayList<>();
        Collections.addAll(listOfAuthorIds, authorsIds);
        return authorRepository.findAllById(listOfAuthorIds).stream().
                map(authorMapper ::authorToAuthorDto)
                .collect(Collectors.toList());
    }
}
