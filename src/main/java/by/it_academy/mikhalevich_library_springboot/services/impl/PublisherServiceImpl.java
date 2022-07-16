package by.it_academy.mikhalevich_library_springboot.services.impl;

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
                                .orElse(null));
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable paged = PageRequest.of(pageNumber - 1, pageSize, sort);
        return publisherRepository.findAll(publisherSpecification, paged).map(publisherMapper::publisherToPublisherDto);
    }

    @Override
    public List<PublisherDto> findAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(publisherMapper::publisherToPublisherDto)
                .collect(Collectors.toList());
    }

    @Override
    public PublisherDto findPublisherById(int id) {
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
    public void deletePublisherById(int id) {
        publisherRepository.deleteById(id);
    }
}
