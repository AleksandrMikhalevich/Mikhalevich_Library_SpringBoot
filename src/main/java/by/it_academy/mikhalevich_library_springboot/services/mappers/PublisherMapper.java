package by.it_academy.mikhalevich_library_springboot.services.mappers;

import by.it_academy.mikhalevich_library_springboot.entities.Publisher;
import by.it_academy.mikhalevich_library_springboot.services.dto.PublisherDto;
import org.mapstruct.*;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 13:40
 */
@Mapper(componentModel = "spring")
public interface PublisherMapper {

    Publisher publisherDtoToPublisher(PublisherDto publisherDto);

    PublisherDto publisherToPublisherDto(Publisher publisher);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Publisher updatePublisherFromPublisherDto(PublisherDto publisherDto, @MappingTarget Publisher publisher);

    @AfterMapping
    default void linkBooks(@MappingTarget Publisher publisher) {
        publisher.getBooks().forEach(book -> book.setPublisher(publisher));
    }
}
