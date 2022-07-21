package by.it_academy.mikhalevich_library_springboot.services.impl;

import by.it_academy.mikhalevich_library_springboot.entities.User;
import by.it_academy.mikhalevich_library_springboot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 18:42
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("Невозможно найти пользователя " + login + "!");
        }
        return new UserDetailsImpl(user);
    }
}
