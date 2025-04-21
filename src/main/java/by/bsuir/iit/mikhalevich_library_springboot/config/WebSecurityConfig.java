package by.bsuir.iit.mikhalevich_library_springboot.config;

import by.bsuir.iit.mikhalevich_library_springboot.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 18:37
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/login", "/", "/index", "/books", "/books/page/*", "/authors", "/authors/page/*",
                        "/genres", "/genres/page/*", "/img/logo.jpg", "/css/**", "/js/**").permitAll()
                .antMatchers("/registration").not().fullyAuthenticated()
                .antMatchers("/users", "/users/page/*", "/books/new-book", "/books/new-book/**", "/books/edit-book",
                        "/books/edit-book/**", "/books/page/*/*", "/authors/new-author", "/authors/new-author/**",
                        "/authors/edit-author", "/authors/edit-author/**", "/authors/page/*/*", "/genres/new-genre",
                        "/genres/edit-genre", "/genres/page/*/*", "/publishers", "/publishers/page/*/*", "/publishers/**",
                        "/uploadFile").hasAuthority("ADMIN")
                .antMatchers("/downloadFile", "/downloadFile/**", "/user-account",
                        "/user-account/**").hasAnyAuthority("ADMIN", "USER")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied")
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/");
        return http.build();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}

