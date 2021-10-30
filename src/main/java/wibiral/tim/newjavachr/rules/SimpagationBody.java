package wibiral.tim.newjavachr.rules;

import wibiral.tim.newjavachr.constraints.Constraint;

import java.util.List;

/**
 * Represents the lambda expression of the body.
 */
public interface SimpagationBody {
    /**
     * Executes the body of the rule.
     * @param head1 The constraints from the head1. Its guaranteed that they were accepted by the guard. These stay in the ConstraintStore.
     * @param head2 The constraints from the head2. Its guaranteed that they were accepted by the guard. These get removed.
     * @param newConstraints Add all constraints to this list, which should be added to the constraint store.
     */
    void execute(Constraint<?>[] head1, Constraint<?>[] head2, List<Constraint<?>> newConstraints);
}
