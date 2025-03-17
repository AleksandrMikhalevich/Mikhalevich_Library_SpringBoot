package by.bsuir.iit.mikhalevich_library_springboot.service.mapper;

import by.bsuir.iit.mikhalevich_library_springboot.entity.Publisher;
import by.bsuir.iit.mikhalevich_library_springboot.service.dto.PublisherDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

}
