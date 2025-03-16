package by.bsuir.iit.mikhalevich_library_springboot.services.interfaces;

/**
 * @author Alex Mikhalevich
 * @created 2022-08-03 12:04
 */
public interface EmailService {

    String sendRegistrationMail(String email, String login, String password);

    String sendUpdateMail(String email, String login, String password);
}
