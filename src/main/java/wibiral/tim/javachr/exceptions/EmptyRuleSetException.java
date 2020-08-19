package wibiral.tim.javachr.exceptions;

import wibiral.tim.javachr.ConstraintSolver;

/**
 * This method gets thrown by the {@link ConstraintSolver} if the set of rules given
 * to the constructor is empty.
 */
public class EmptyRuleSetException extends RuntimeException {
    public EmptyRuleSetException(String errorMessage){
        super(errorMessage);
    }
}
