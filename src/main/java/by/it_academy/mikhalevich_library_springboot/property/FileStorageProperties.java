package by.it_academy.mikhalevich_library_springboot.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-21 14:27
 */
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileStorageProperties {

    private String uploadDir;
}
