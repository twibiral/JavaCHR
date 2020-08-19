package wibiral.tim.javachr.exceptions;

public class AlreadyBoundException extends RuntimeException {
    public AlreadyBoundException(String errorMessage){
        super(errorMessage);
    }
}