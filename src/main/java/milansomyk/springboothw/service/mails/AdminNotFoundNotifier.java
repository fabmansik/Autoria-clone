package milansomyk.springboothw.service.mails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminNotFoundNotifier {
    private final MailSender mailSender;
    private final UserRepository userRepository;
    public ResponseContainer sendMail(String type, String object, ResponseContainer responseContainer){
        if(!StringUtils.hasText(type)){
            log.error("type is null");
            return responseContainer.setErrorMessageAndStatusCode("type is null",HttpStatus.BAD_REQUEST.value());
        }
        if(!StringUtils.hasText(object)){
            log.error("object is null");
            return responseContainer.setErrorMessageAndStatusCode("object is null", HttpStatus.BAD_REQUEST.value());
        }
        List<User> admins;
        try {
            admins = userRepository.findByRole(Role.ADMIN.name()).orElse(null);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(CollectionUtils.isEmpty(admins)){
            log.error("managers not found");
            return responseContainer.setErrorMessageAndStatusCode("managers not found",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        List<String> adminsEmails = admins.stream().map(User::getEmail).toList();
        for (String adminEmail : adminsEmails) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("milansomyk@gmail.com");
            message.setTo(adminEmail);
            message.setSubject(type+" with name: "+object+" not found in database");
            message.setText(type+": "+object+" not found in database. \n Add it please");
            try {
                mailSender.send(message);
            } catch (Exception e){
                log.error(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        responseContainer.setSuccessResult("admin was notified about missing "+type.toLowerCase()+": "+object.toLowerCase());
        return responseContainer;
    }
}
