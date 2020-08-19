package wibiral.tim.javachr.rules;

import wibiral.tim.javachr.Constraint;

/**
 * Represents the lambda expression of the guard.
 */
public interface Guard {
    boolean check(Constraint<?>[] constraints);
}
