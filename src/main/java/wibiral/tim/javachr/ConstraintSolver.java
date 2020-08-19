package wibiral.tim.javachr;

import wibiral.tim.javachr.exceptions.EmptyRuleSetException;
import wibiral.tim.javachr.rules.Rule;

import java.util.List;

/**
 * Is used to apply rules to the Constraints.
 */
public abstract class ConstraintSolver {
    protected final RuleSet rules;

    private static final String ERROR_MESSAGE = "The set of rules must contain at least one rule!";

    public ConstraintSolver(RuleSet rules){
        if(rules.isEmpty()) throw new EmptyRuleSetException(ERROR_MESSAGE);

        this.rules = rules;
        this.rules.block();
    }

    public ConstraintSolver(List<Rule> rules){
        if(rules.isEmpty()) throw new EmptyRuleSetException(ERROR_MESSAGE);

        this.rules = new RuleSet(rules);
        this.rules.block();
    }

    public ConstraintSolver(Rule... rules){
        if(rules.length < 1) throw new EmptyRuleSetException(ERROR_MESSAGE);

        this.rules = new RuleSet(rules);
        this.rules.block();
    }

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
}
