package by.it_academy.mikhalevich_library_springboot.services.impl;

import by.it_academy.mikhalevich_library_springboot.services.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author Alex Mikhalevich
 * @created 2022-08-03 12:23
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendRegistrationMail(String email, String login, String password) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText("Добрый день! Вы успешно зарегистрировались в интернет-библиотеке Bookаньеры." + "\n"
                    + "Ваш логин: " + login + "\n" + "Ваш пароль: " + password);
            mailMessage.setSubject("Регистрация на Bookаньерах");
            javaMailSender.send(mailMessage);
            return "Письмо с данными о регистрации направлены на указанную при регистрации электронную почту." + "\n"
                    + "Теперь вы можете использовать эти данные для входа на сайт.";
        } catch (Exception e) {
            return "Ошибка отправки письма!";
        }
    }

    @Override
    public String sendUpdateMail(String email, String login, String password) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText("Добрый день! Вы успешно обновили профиль в интернет-библиотеке Bookаньеры." + "\n"
                    + "Ваш логин для входа: " + login + "\n" + "Ваш новый пароль: " + password);
            mailMessage.setSubject("Регистрация на Bookаньерах");
            javaMailSender.send(mailMessage);
            return "Письмо с обновленными данными профиля направлены на указанную вами электронную почту.";
        } catch (Exception e) {
            return "Ошибка отправки письма!";
        }
    }
}
