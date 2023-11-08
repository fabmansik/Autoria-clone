package milansomyk.springboothw.exceptions;

public class NotPremiumAccountException extends Exception{
    public NotPremiumAccountException(String msg){
        super(msg);
    }
}
