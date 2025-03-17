package by.bsuir.iit.mikhalevich_library_springboot.service.impl;

import by.bsuir.iit.mikhalevich_library_springboot.entity.Author;
import by.bsuir.iit.mikhalevich_library_springboot.filter.AuthorFilter;
import by.bsuir.iit.mikhalevich_library_springboot.repository.AuthorRepository;
import by.bsuir.iit.mikhalevich_library_springboot.repository.PublisherRepository;
import by.bsuir.iit.mikhalevich_library_springboot.service.interf.AuthorService;
import by.bsuir.iit.mikhalevich_library_springboot.service.mapper.AdditionalMapper;
import by.bsuir.iit.mikhalevich_library_springboot.service.mapper.PublisherMapper;
import by.bsuir.iit.mikhalevich_library_springboot.specification.AuthorSpecification;
import by.bsuir.iit.mikhalevich_library_springboot.service.dto.AuthorDto;
import by.bsuir.iit.mikhalevich_library_springboot.service.mapper.AuthorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
