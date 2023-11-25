package milansomyk.springboothw.service.mails;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor

public class AdminNotFoundNotifier {
    private final MailSender mailSender;
    private final UserRepository userRepository;
    public void sendMail(String type, String object){
        List<User> managers = userRepository.findByRole(Role.ADMIN.name()).orElse(null);
        List<String> adminsEmails = managers.stream().map(User::getEmail).toList();
        for (String adminEmail : adminsEmails) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("milansomyk@gmail.com");
            message.setTo(adminEmail);
            message.setSubject(type+" with name: "+object+" not found in database");
            message.setText(type+": "+object+" not found in database. \n Add it please");
            mailSender.send(message);
        }
    }
}
