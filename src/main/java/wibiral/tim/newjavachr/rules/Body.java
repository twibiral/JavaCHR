package wibiral.tim.newjavachr.rules;

import wibiral.tim.newjavachr.Constraint;

import java.util.List;

/**
 * Represents the lambda expression of the body.
 */
public interface Body {
    /**
     * Executes the body of the rule. Example:
     * {@code head, newConstraints) -> newConstraints.add(new Constraint<Integer>(42)}
     * @param head The constraints from the head. Its guaranteed that they were accepted by the guard.
     * @param newConstraints Add all constraints to this list, which should be added to the constraint store.
     */
    void execute(Constraint<?>[] head, List<Constraint<?>> newConstraints);
}
