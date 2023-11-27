package milansomyk.springboothw.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import milansomyk.springboothw.dto.ErrorDto;
import milansomyk.springboothw.dto.response.ResponseContainer;
import org.hibernate.NonUniqueResultException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDto> handleError(MethodArgumentNotValidException e, WebRequest webRequest){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.builder()
                        .messages(e.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList())
                        .build());
    }
    @ExceptionHandler({NonUniqueResultException.class})
    public ResponseEntity<ErrorDto> handleError(NonUniqueResultException e, WebRequest webRequest){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.builder()
                        .messages(List.of(e.getMessage()))
                        .build());
    }
    @ExceptionHandler({IOException.class})
    public ResponseEntity<ErrorDto> handleError(IOException e, WebRequest webRequest){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.builder()
                        .messages(Arrays.asList(e.getMessage()))
                        .build());
    }
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> exception(NoHandlerFoundException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorDto.builder()
                        .messages(Arrays.asList(e.getMessage()))
                        .build());
    }
    @ExceptionHandler(value = {ExpiredJwtException.class})
    public ResponseEntity<ResponseContainer> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        String requestUri = ((ServletWebRequest)request).getRequest().getRequestURI().toString();
        ResponseContainer responseContainer = new ResponseContainer(requestUri, HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }

}
