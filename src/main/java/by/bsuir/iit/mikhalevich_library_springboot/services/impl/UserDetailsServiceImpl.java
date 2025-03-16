package by.bsuir.iit.mikhalevich_library_springboot.services.impl;

import by.bsuir.iit.mikhalevich_library_springboot.entities.Role;
import by.bsuir.iit.mikhalevich_library_springboot.entities.User;
import by.bsuir.iit.mikhalevich_library_springboot.filters.UserFilter;
import by.bsuir.iit.mikhalevich_library_springboot.repositories.UserRepository;
import by.bsuir.iit.mikhalevich_library_springboot.services.mappers.UserMapper;
import by.bsuir.iit.mikhalevich_library_springboot.specifications.UserSpecification;
import by.bsuir.iit.mikhalevich_library_springboot.services.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 18:42
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("Невозможно найти пользователя " + login + "!");
        }
        return new UserDetailsImpl(userMapper.userToUserDto(user));
    }

    public boolean saveUser(UserDto userDto) {
        User userFromDB = userRepository.findByLogin(userDto.getLogin());
        if (userFromDB != null) {
            return false;
        }
        User user = userMapper.userDtoToUser(userDto);
        user.setRoles(Collections.singleton(new Role(1, "USER")));
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public void updateUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        user.setRoles(Collections.singleton(new Role(1, "USER")));
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    public UserDto findUserByLogin(String login) {
        User user = userRepository.findByLogin(login);
        return userMapper.userToUserDto(user);
    }

    public UserDto findUserById(Integer id) {
        return userRepository.findById(id).map(userMapper::userToUserDto).orElse(null);
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    public Page<UserDto> findAllUsersPaginatedSortedFiltered(UserFilter userFilter, int pageNumber, int pageSize, String sortField, String sortDirection) {
        Specification<User> userSpecification =
                Specification
                        .where(Optional.ofNullable(userFilter.getLoginFilter())
                                .map(UserSpecification::getUserByLoginSpec)
                                .orElse(null))
                        .and(Optional.ofNullable(userFilter.getEmailFilter())
                                .map(UserSpecification::getUserByEmailSpec)
                                .orElse(null));
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable paged = PageRequest.of(pageNumber - 1, pageSize, sort);
        return userRepository.findAll(userSpecification, paged).map(userMapper::userToUserDto);
    }
}
