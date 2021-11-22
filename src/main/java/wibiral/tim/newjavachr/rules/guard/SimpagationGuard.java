package wibiral.tim.newjavachr.rules.guard;

import wibiral.tim.newjavachr.constraints.Constraint;

/**
 * Represents the lambda expression of the guard for Simpagation which uses two heads.
 */
public interface SimpagationGuard {
    boolean check(Constraint<?>[] head1, Constraint<?>[] head2);
}
