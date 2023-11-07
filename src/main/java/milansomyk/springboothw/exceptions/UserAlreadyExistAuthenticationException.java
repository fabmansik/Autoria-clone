package milansomyk.springboothw.exceptions;

import javax.naming.AuthenticationException;

public class UserAlreadyExistAuthenticationException extends AuthenticationException {
    public UserAlreadyExistAuthenticationException(final String msg){
        super(msg);
    }
}
