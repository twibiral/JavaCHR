package javachr.rules.guard;

import javachr.constraints.Constraint;

/**
 * Represents the lambda expression of the guard for Simpagation which uses two heads.
 */
public interface SimpagationGuard {
    boolean check(Constraint<?>[] head1, Constraint<?>[] head2);
}
