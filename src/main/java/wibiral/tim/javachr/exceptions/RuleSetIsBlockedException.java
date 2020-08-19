package wibiral.tim.javachr.exceptions;

/**
 * Is thrown if someone tries to add a rule after a solver with the rule set was created.
 */
public class RuleSetIsBlockedException extends RuntimeException {
    public RuleSetIsBlockedException(String errorMessage){
        super(errorMessage);
    }
}
