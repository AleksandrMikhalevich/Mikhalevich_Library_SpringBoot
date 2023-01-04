package by.it_academy.mikhalevich_library_springboot.services.dto;

import lombok.Data;

import javax.persistence.Transient;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 15:38
 */
@Data
public class UserDto implements Serializable {

    private final Integer id;
    @NotBlank(message = "Поле не должно быть пустым")
    @Size(min = 3, max = 16, message = "Длина логина должна быть от 3 до 16 символов")
    @Pattern(regexp = "[a-zA-Z]*", message = "Допустимы только латинские буквы")
    private final String login;
    private final String password;
    @Transient
    private String passwordConfirm;
    @NotBlank(message = "Поле не должно быть пустым")
    @Pattern(regexp = "^(?=.{6,64}$)(?![_!#$%&'*+/=?`{|}~^.-])+[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "Адрес электронной почты должен быть в формате example@example.com")
    private final String email;
    private final Set<RoleDto> roles;

    @Data
    public static class RoleDto implements Serializable {
        private final Integer id;
        private final String name;
    }
}
