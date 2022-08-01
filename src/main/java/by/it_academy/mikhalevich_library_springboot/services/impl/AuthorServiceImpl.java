package by.it_academy.mikhalevich_library_springboot.services.impl;

import by.it_academy.mikhalevich_library_springboot.entities.Author;
import by.it_academy.mikhalevich_library_springboot.repositories.AuthorRepository;
import by.it_academy.mikhalevich_library_springboot.filters.AuthorFilter;
import by.it_academy.mikhalevich_library_springboot.repositories.PublisherRepository;
import by.it_academy.mikhalevich_library_springboot.services.dto.AuthorDto;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.AuthorService;
import by.it_academy.mikhalevich_library_springboot.services.mappers.AdditionalMapper;
import by.it_academy.mikhalevich_library_springboot.services.mappers.AuthorMapper;
import by.it_academy.mikhalevich_library_springboot.services.mappers.PublisherMapper;
import by.it_academy.mikhalevich_library_springboot.specifications.AuthorSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-12 12:25
 */
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorMapper authorMapper;
    private final PublisherMapper publisherMapper;
    private final AdditionalMapper additionalMapper;

    @Override
    public Page<AuthorDto> findAllAuthorsPaginatedSortedFiltered(AuthorFilter authorFilter, int pageNumber, int pageSize, String sortField, String sortDirection) {
        Specification<Author> authorSpecification =
                Specification
                        .where(Optional.ofNullable(authorFilter.getFirstNameFilter())
                                .map(AuthorSpecification::getAuthorByFirstNameSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(authorFilter.getSecondNameFilter())
                                .map(AuthorSpecification::getAuthorBySecondNameSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(authorFilter.getSurnameFilter())
                                .map(AuthorSpecification::getAuthorBySurnameSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(authorFilter.getCountryFilter())
                                .map(AuthorSpecification::getAuthorByCountrySpec)
                                .orElse(null))
                        .and(Optional.ofNullable(authorFilter.getBookFilter())
                                .map(AuthorSpecification::getAuthorByBookTitleSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(authorFilter.getPublisherFilter())
                                .map(AuthorSpecification::getAuthorByPublisherNameSpec)
                                .orElse(null));
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable paged = PageRequest.of(pageNumber - 1, pageSize, sort);
        return authorRepository.findAll(authorSpecification, paged).map(authorMapper::authorToAuthorDto);
    }

    @Override
    public AuthorDto findAuthorById(Integer id) {
        return authorRepository.findById(id).map(authorMapper::authorToAuthorDto).orElse(null);
    }

    @Override
    @Transactional
    public void addAuthor(Integer[] publishersIds, String surname, String firstName, String secondName, String country) {
        List<Integer> listOfPublisherIds = new ArrayList<>();
        Collections.addAll(listOfPublisherIds, publishersIds);
        Set<AuthorDto.PublisherDto> publisherDtoSet = publisherRepository.findAllById(listOfPublisherIds).stream()
                .map(publisherMapper::publisherToPublisherDto)
                .map(additionalMapper::toAuthorPublisherDto)
                .collect(Collectors.toSet());
        AuthorDto authorDto = AuthorDto.builder()
                .surname(surname)
                .firstName(firstName)
                .secondName(secondName)
                .country(country)
                .publishers(publisherDtoSet)
                .build();
        authorRepository.save(authorMapper.authorDtoToAuthor(authorDto));
    }

    @Override
    public void updateAuthor(Integer id, Integer[] publishersIds, String surname, String firstName, String secondName, String country) {
        List<Integer> listOfPublisherIds = new ArrayList<>();
        Collections.addAll(listOfPublisherIds, publishersIds);
        Set<AuthorDto.PublisherDto> publisherDtoSet = publisherRepository.findAllById(listOfPublisherIds).stream()
                .map(publisherMapper::publisherToPublisherDto)
                .map(additionalMapper::toAuthorPublisherDto)
                .collect(Collectors.toSet());
        AuthorDto authorDto = AuthorDto.builder()
                .id(id)
                .surname(surname)
                .firstName(firstName)
                .secondName(secondName)
                .country(country)
                .publishers(publisherDtoSet)
                .build();
        authorRepository.save(authorMapper.authorDtoToAuthor(authorDto));
    }

    @Override
    public void deleteAuthorById(Integer id) {
        authorRepository.deleteById(id);
    }

    @Override
    public List<AuthorDto> chooseAuthors(Integer[] authorsIds) {
        List<Integer> listOfAuthorIds = new ArrayList<>();
        Collections.addAll(listOfAuthorIds, authorsIds);
        return authorRepository.findAllById(listOfAuthorIds).stream().
                map(authorMapper::authorToAuthorDto)
                .collect(Collectors.toList());
    }
}
