package wibiral.tim.javachr.rules;

import wibiral.tim.javachr.Constraint;

import java.util.List;

/**
 * Represents the lambda expression of the body.
 */
public interface SimpagationBody {
    /**
     * Executes the body of the rule.
     * @param constraintsHead1 The constraints from the head1. Its guaranteed that they were accepted by the guard. This are the ones that stay in the ConstraintStore.
     * @param constraintsHead2 The constraints from the head2. Its guaranteed that they were accepted by the guard. This are the ones that get removed.
     * @param newConstraints Add all constraints to this list, which should be added to the constraint store.
     */
    void execute(Constraint<?>[] constraintsHead1, Constraint<?>[] constraintsHead2, List<Constraint<?>> newConstraints);
}
