package by.bsuir.iit.mikhalevich_library_springboot.service.interf;

import by.bsuir.iit.mikhalevich_library_springboot.filter.PublisherFilter;
import by.bsuir.iit.mikhalevich_library_springboot.service.dto.PublisherDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-15 18:19
 */
public interface PublisherService {

    Page<PublisherDto> findAllPublishersPaginatedSortedFiltered(PublisherFilter publisherFilter, int pageNumber, int pageSize, String sortField, String sortDirection);

    PublisherDto findPublisherById(Integer id);

    void addPublisher(PublisherDto publisherDto);

    void updatePublisher(PublisherDto publisherDto);

    void deletePublisherById(Integer id);

    List<PublisherDto> choosePublishers(Integer[] publishersIds);
}
