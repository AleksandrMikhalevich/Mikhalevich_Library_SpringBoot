package by.it_academy.mikhalevich_library_springboot.services.impl;

import by.it_academy.mikhalevich_library_springboot.entities.Role;
import by.it_academy.mikhalevich_library_springboot.entities.User;
import by.it_academy.mikhalevich_library_springboot.repositories.UserRepository;
import by.it_academy.mikhalevich_library_springboot.services.dto.UserDto;
import by.it_academy.mikhalevich_library_springboot.services.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
}
