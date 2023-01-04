package by.it_academy.mikhalevich_library_springboot.services.mappers;

import by.it_academy.mikhalevich_library_springboot.entities.DBFile;
import by.it_academy.mikhalevich_library_springboot.services.dto.DBFileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-22 18:55
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface DBFileMapper {


    @Mapping(target = "book", source = "bookDto")
    DBFile DBFileDtoToDBFile(DBFileDto DBFileDto);

    @Mapping(target = "bookDto", source = "book")
    DBFileDto DBFileToDBFileDto(DBFile DBFile);

}
