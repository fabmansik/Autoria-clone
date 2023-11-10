package milansomyk.springboothw.service;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ManagerModerationNotifier {
    private final MailSender mailSender;
    private final UserRepository userRepository;
    public void sendMail(Car car){
        List<User> managers = userRepository.findByRole(Role.MANAGER.name());
        List<String> managerEmails = managers.stream().map(User::getEmail).toList();
        for (String managerEmail : managerEmails) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("milansomyk@gmail.com");
            message.setTo(managerEmail);
            message.setSubject("Moderation failed while posting a publish");
            message.setText(car.toString());
        }
    }
}
