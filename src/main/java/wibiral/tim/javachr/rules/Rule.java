package wibiral.tim.javachr.rules;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;

import java.util.List;

public abstract class Rule {
    protected final int nrConstraintsInHead;

    public Rule(int nrConstraintsInHead){
        this.nrConstraintsInHead = nrConstraintsInHead;
    }

    public int headSize(){
        return nrConstraintsInHead;
    }

    /**
     * Applies the rule to the {@link ConstraintStore}.
     * DOES NOT CHECK IF THE GIVEN CONSTRAINTS ARE ACCEPTED TO IMPROVE PERFORMANCE!
     * If there are wrong constraints in the ConstraintStore the method may throws an exception but may just executes
     * the wrong way.
     * @param store The constraint store to which the rule should be applied.
     * @return true if the rule was successfully applied to any constraints.
     */
    public abstract boolean apply(ConstraintStore store);

    /**
     * @param constraints The store to check.
     * @return True if the guard of the Rule accepts the given {@link ConstraintStore}.
     */
    public abstract boolean accepts(ConstraintStore constraints);

    /**
     * @param constraints The store to check.
     * @return True if the guard of the Rule accepts the given {@link Constraint}s.
     */
    public abstract boolean accepts(List<Constraint<?>> constraints);
}
