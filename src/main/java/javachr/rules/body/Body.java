package javachr.rules.body;

import javachr.constraints.Constraint;

/**
 * Represents the lambda expression of the body.
 */
public interface Body {
    /**
     * Executes the body of the rule. Example:
     * {@code head, newConstraints) -> newConstraints.add(new Constraint<Integer>(42)}
     * @param head The constraints from the head. Its guaranteed that they were accepted by the guard.
     * @param newConstraints Add all constraints to this data structure, which should be added to the constraint store.
     */
    void execute(Constraint<?>[] head, BodyStore newConstraints);
}
