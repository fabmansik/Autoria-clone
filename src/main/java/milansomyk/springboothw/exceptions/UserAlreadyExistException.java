package milansomyk.springboothw.exceptions;

public class UserAlreadyExistException extends Exception{
    public UserAlreadyExistException(String errorMessage){
        super(errorMessage);
    }
}
