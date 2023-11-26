package milansomyk.springboothw.service.mails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ManagerModerationNotifier {
    private final MailSender mailSender;
    private final UserRepository userRepository;
    public ResponseContainer sendMail(Car car, ResponseContainer responseContainer){
        List<User> managers;
        try {
            managers = userRepository.findByRole(Role.MANAGER.name()).orElse(null);
        } catch (Exception e){
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(managers== null){
            return responseContainer.setErrorMessageAndStatusCode("Failed to find managers",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        List<String> managerEmails = managers.stream().map(User::getEmail).toList();
        System.out.println(car);
        for (String managerEmail : managerEmails) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("milansomyk@gmail.com");
            message.setTo(managerEmail);
            message.setSubject("Moderation failed while posting a publish a car with id: "+car.getId());
            message.setText("ID: "+car.getId()+"\n"+"Details: "+car.getDetails());
            try {
                mailSender.send(message);
            } catch (Exception e){
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        responseContainer.setSuccessResult("Manager was notified");
        return responseContainer;
    }
}
