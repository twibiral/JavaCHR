package wibiral.tim.newjavachr.exceptions;

/**
 * This exception is thrown if the constructor of a rule fails because the rule definition is incorrect.
 * (e.g. The size of Head1 is bigger than the size of Head1+Head2)
 */
public class RuleDefinitionFailed extends RuntimeException {
    public RuleDefinitionFailed(String errorMessage){
        super(errorMessage);
    }
}
