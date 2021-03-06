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

    @Mapping(target = "address", source = ".")
    @Mapping(source = "addressCountry", target = "address.country")
    @Mapping(source = "addressCity", target = "address.city")
    @Mapping(source = "addressStreet", target = "address.street")
    @Mapping(source = "addressHouse", target = "address.house")
    @Mapping(source = "addressZipcode", target = "address.zipcode")
    Publisher publisherDtoToPublisher(PublisherDto publisherDto);

    @InheritInverseConfiguration(name = "publisherDtoToPublisher")
    PublisherDto publisherToPublisherDto(Publisher publisher);

    @Mapping(target = "address", source = ".")
    @InheritConfiguration(name = "publisherDtoToPublisher")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Publisher updatePublisherFromPublisherDto(PublisherDto publisherDto, @MappingTarget Publisher publisher);
}
