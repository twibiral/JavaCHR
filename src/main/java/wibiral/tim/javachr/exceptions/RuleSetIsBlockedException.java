package wibiral.tim.javachr.exceptions;

/**
 * Is thrown if someone tries to add a rule after a handler with the rule set was created.
 * That should prevent that rules are added to a handler while the handler is running.
 */
public class RuleSetIsBlockedException extends RuntimeException {
    public RuleSetIsBlockedException(String errorMessage){
        super(errorMessage);
    }
}
