package wibiral.tim.newjavachr;

import java.util.List;

/**
 * Constraint solvers apply a set of rules to some given constraints.
 * Classes that implement this interface provide different "solve" methods which return a list with constraints after the rules were applied.
 */
public interface ConstraintSolver {
    /**
     * Applies the defined rules to the given constraints.
     * @param constraints the constraints you want the rules applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    List<Constraint<?>> solve(Constraint<?>... constraints);

    /**
     * Applies the defined rules to the given constraints.
     * @param constraints the constraints you want the rules applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    List<Constraint<?>> solve(List<?>... constraints);

    /**
     * Applies the defined rules to the given objects.
     * @param values Some objects you want the rules be applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    <T> List<Constraint<?>> solve(T... values);

}
