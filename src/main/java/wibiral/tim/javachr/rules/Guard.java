package wibiral.tim.javachr.rules;

import wibiral.tim.javachr.constraints.Constraint;

/**
 * Represents the lambda expression of the guard.
 */
public interface Guard {
    /**
     * Checks if defined conditions are true for the constraints in the array. Example:
     * head -> head[0] > head[1]
     * or
     * head -> (int) head[0].value() == 42
     * @param head An array with constraints which represent the constraints from the head.
     * @return True if the constraints fulfill the conditions.
     */
    boolean check(Constraint<?>[] head);
}
