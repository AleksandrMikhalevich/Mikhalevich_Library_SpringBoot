package by.bsuir.iit.mikhalevich_library_springboot.service.mapper;

import by.bsuir.iit.mikhalevich_library_springboot.entity.User;
import by.bsuir.iit.mikhalevich_library_springboot.service.dto.UserDto;
import org.mapstruct.Mapper;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 13:41
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    User userDtoToUser(UserDto userDto);

    UserDto userToUserDto(User user);
}
