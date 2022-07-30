package by.it_academy.mikhalevich_library_springboot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 18:37
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/login", "/", "/index", "/books", "/books/page/*", "/authors", "/authors/page/*",
                        "/genres", "/genres/page/*", "/img/logo.jpg").permitAll()
                .antMatchers("/registration").not().fullyAuthenticated()
                .antMatchers("/books/new-book", "/books/new-book/**", "/books/edit-book",
                        "/books/edit-book/**", "/books/page/*/*", "/authors/new-author", "/authors/new-author/**",
                        "/authors/edit-author", "/authors/edit-author/**", "/authors/page/*/*", "/genres/new-genre",
                        "/genres/edit-genre", "/genres/page/*/*", "/publishers", "/publishers/page/*/*", "/publishers/**",
                        "/uploadFile").hasAuthority("ADMIN")
                .antMatchers("/downloadFile", "/downloadFile/**").hasAnyAuthority("ADMIN", "USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/");
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}

