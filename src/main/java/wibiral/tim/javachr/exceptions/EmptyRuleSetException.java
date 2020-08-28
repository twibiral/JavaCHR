package wibiral.tim.javachr.exceptions;

import wibiral.tim.javachr.ConstraintHandler;

/**
 * This method gets thrown by the {@link ConstraintHandler} if the set of rules given
 * to the constructor is empty.
 */
public class EmptyRuleSetException extends RuntimeException {
    public EmptyRuleSetException(String errorMessage){
        super(errorMessage);
    }
}
