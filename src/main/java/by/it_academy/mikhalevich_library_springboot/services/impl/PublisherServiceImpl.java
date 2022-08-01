package by.it_academy.mikhalevich_library_springboot.services.impl;

import by.it_academy.mikhalevich_library_springboot.entities.Author;
import by.it_academy.mikhalevich_library_springboot.entities.Publisher;
import by.it_academy.mikhalevich_library_springboot.filters.PublisherFilter;
import by.it_academy.mikhalevich_library_springboot.repositories.PublisherRepository;
import by.it_academy.mikhalevich_library_springboot.services.dto.PublisherDto;
import by.it_academy.mikhalevich_library_springboot.services.interfaces.PublisherService;
import by.it_academy.mikhalevich_library_springboot.services.mappers.PublisherMapper;
import by.it_academy.mikhalevich_library_springboot.specifications.PublisherSpecification;
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
import java.util.stream.Collectors;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 18:27
 */
@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    @Override
    public Page<PublisherDto> findAllPublishersPaginatedSortedFiltered(PublisherFilter publisherFilter, int pageNumber, int pageSize, String sortField, String sortDirection) {
        Specification<Publisher> publisherSpecification =
                Specification
                        .where(Optional.ofNullable(publisherFilter.getNameFilter())
                                .map(PublisherSpecification::getPublisherByNameSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(publisherFilter.getCountryFilter())
                                .map(PublisherSpecification::getPublisherByCountrySpec)
                                .orElse(null))
                        .and(Optional.ofNullable(publisherFilter.getCityFilter())
                                .map(PublisherSpecification::getPublisherByCitySpec)
                                .orElse(null))
                        .and(Optional.ofNullable(publisherFilter.getStreetFilter())
                                .map(PublisherSpecification::getPublisherByStreetSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(publisherFilter.getHouseFilter())
                                .map(PublisherSpecification::getPublisherByHouseSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(publisherFilter.getZipcodeFilter())
                                .map(PublisherSpecification::getPublisherByZipcodeSpec)
                                .orElse(null));
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable paged = PageRequest.of(pageNumber - 1, pageSize, sort);
        return publisherRepository.findAll(publisherSpecification, paged).map(publisherMapper::publisherToPublisherDto);
    }

    @Override
    public PublisherDto findPublisherById(Integer id) {
        return publisherRepository.findById(id).map(publisherMapper::publisherToPublisherDto).orElse(null);
    }

    @Override
    public void addPublisher(PublisherDto publisherDto) {
        publisherRepository.save(publisherMapper.publisherDtoToPublisher(publisherDto));
    }

    @Override
    public void updatePublisher(PublisherDto publisherDto) {
        publisherRepository.save(publisherMapper.publisherDtoToPublisher(publisherDto));
    }

    @Override
    @Transactional
    public void deletePublisherById(Integer id) {
        Publisher publisher = publisherRepository.findById(id).orElse(null);
        assert publisher != null;
        for (Author author : publisher.getAuthors()) {
            publisher.removeAuthor(author);
        }
        publisherRepository.save(publisher);
        publisherRepository.deleteById(id);
    }

    @Override
    public List<PublisherDto> choosePublishers(Integer[] publishersIds) {
        List<Integer> listOfPublisherIds = new ArrayList<>();
        Collections.addAll(listOfPublisherIds, publishersIds);
        return publisherRepository.findAllById(listOfPublisherIds).stream()
                .map(publisherMapper::publisherToPublisherDto)
                .collect(Collectors.toList());
    }
}
