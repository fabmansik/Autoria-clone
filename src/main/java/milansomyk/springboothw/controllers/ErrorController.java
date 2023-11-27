package milansomyk.springboothw.controllers;

import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.response.ResponseContainer;
import org.hibernate.NonUniqueResultException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;


@RestControllerAdvice
@Slf4j
public class ErrorController {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseContainer> handleError(MethodArgumentNotValidException e){
        log.error(e.getMessage());
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setErrorMessageAndStatusCode(e.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString(),HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @ExceptionHandler({NonUniqueResultException.class})
    public ResponseEntity<ResponseContainer> handleError(NonUniqueResultException e){
        log.error(e.getMessage());
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @ExceptionHandler({IOException.class})
    public ResponseEntity<ResponseContainer> handleError(IOException e){
        log.error(e.getMessage());
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ResponseEntity<ResponseContainer> exception(NoHandlerFoundException e) {
        log.error(e.getMessage());
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ResponseContainer> exception(RuntimeException e){
        log.error(e.getMessage());
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
}
