package wibiral.tim.javachr;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simplification;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Is used to apply rules to the Constraints.
 */
public abstract class ConstraintHandler {
    protected final LinkedList<Rule> rules = new LinkedList<>();

    private static final String ERROR_MESSAGE = "The set of rules must contain at least one rule!";

    public ConstraintHandler(Rule rule){
        if(rule == null) throw new NullPointerException("Rule can't be null!");

        this.rules.add(rule);
    }

    public ConstraintHandler(Rule... rules){
        Collections.addAll(this.rules, rules);
    }

    public ConstraintHandler() { /* nothing to do */ }

    /**
     * Applies the given rules to the given constraints.
     * The computation of the solution happens in-place.
     * @param store A {@link ConstraintStore} with the constraints of the problem set.
     * @return A {@link ConstraintStore} with the constraints of the solution set.
     */
    public abstract ConstraintStore solve(ConstraintStore store);

    /**
     * Just like solve(ConstraintStore store) but accepts an array.
     * @param values Some objects to which the rules should be applied.
     * @param <T> The type of the objects.
     * @return The {@link ConstraintStore} with the constraints of the solution set.
     */
    public <T> ConstraintStore solve(T... values){
        ConstraintStore store = new ConstraintStore();
        for (T t : values){
            store.add(new Constraint<>(t));
        }

        return solve(store);
    }

    /**
     * @return true if no rules are set for this handler, otherwise false.
     */
    public boolean isEmpty(){
        return rules.isEmpty();
    }

    /**
     * Adds a rule to the constraint handler.
     * @param rule The rule to add.
     * @return True if successfully added.
     */
    public boolean addRule(Rule rule){
        return rules.add(rule);
    }

    @Override
    public String toString() {
        int nrOfSimplifications = 0;
        int nrOfPropagations = 0;
        int nrOfSimpagations = 0;

        for(Rule rule : rules){
            if(rule instanceof Simplification)
                nrOfSimplifications++;
            else if(rule instanceof Propagation)
                nrOfPropagations++;
            else
                nrOfSimpagations++;
        }
        return String.format(
                "Simplifications: %d%n" +
                        "Propagations: %d%n" +
                        "Simpagations: %d%n"
                , nrOfSimplifications, nrOfPropagations, nrOfSimpagations
        );
    }
}
