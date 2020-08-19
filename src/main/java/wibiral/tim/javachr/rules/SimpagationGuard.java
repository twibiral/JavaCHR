package wibiral.tim.javachr.rules;

import wibiral.tim.javachr.constraints.Constraint;

/**
 * Represents the lambda expression of the guard for Simpagation which uses two heads.
 */
public interface SimpagationGuard {
    boolean check(Constraint<?>[] constraintsHead1, Constraint<?>[] constraintsHead2);
}
