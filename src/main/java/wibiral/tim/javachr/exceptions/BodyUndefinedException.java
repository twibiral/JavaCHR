package wibiral.tim.javachr.exceptions;

import wibiral.tim.javachr.rules.Rule;

/**
 * This method gets thrown by a {@link Rule} if it gets applied but no body was defined.
 */
public class BodyUndefinedException extends RuntimeException {
    public BodyUndefinedException(String errorMessage){
        super(errorMessage);
    }
}
