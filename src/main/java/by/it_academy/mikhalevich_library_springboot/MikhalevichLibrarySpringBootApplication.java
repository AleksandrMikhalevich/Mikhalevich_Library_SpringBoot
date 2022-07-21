package by.it_academy.mikhalevich_library_springboot;

import by.it_academy.mikhalevich_library_springboot.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class MikhalevichLibrarySpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MikhalevichLibrarySpringBootApplication.class, args);
    }
}
