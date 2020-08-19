package wibiral.tim.javachr.exceptions;

/**
 * This message is thrown by the rules if the user tries to set a new body/guard if the body/guard is already set.
 */
public class AlreadyDefinedException extends RuntimeException {
    public AlreadyDefinedException(String errorMessage){
        super(errorMessage);
    }
}
