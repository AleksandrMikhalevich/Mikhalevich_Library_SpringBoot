package by.it_academy.mikhalevich_library_springboot.services.interfaces;

import by.it_academy.mikhalevich_library_springboot.filters.PublisherFilter;
import by.it_academy.mikhalevich_library_springboot.services.dto.PublisherDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 18:19
 */
public interface PublisherService {

    Page<PublisherDto> findAllPublishersPaginatedSortedFiltered(PublisherFilter publisherFilter, int pageNumber, int pageSize, String sortField, String sortDirection);

    List<PublisherDto> findAllPublishers();

    PublisherDto findPublisherById(int id);

    void addPublisher(PublisherDto publisherDto);

    void updatePublisher(PublisherDto publisherDto);

    void deletePublisherById(int id);

    List<PublisherDto> choosePublishers(Integer[] publishersIds);
}
