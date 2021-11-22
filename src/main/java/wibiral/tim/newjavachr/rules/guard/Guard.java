package wibiral.tim.newjavachr.rules.guard;

import wibiral.tim.newjavachr.constraints.Constraint;

/**
 * Represents the lambda expression of the guard.
 */
public interface Guard {
    /**
     * Checks if defined conditions are true for the constraints in the array. Example:
     * {@code head -> head[0] > head[1]}
     * or
     * {@code head -> (int) head[0].value() == 42}
     * @param head An array with constraints which represent the constraints from the head.
     * @return True if the constraints fulfill the conditions.
     */
    boolean check(Constraint<?>[] head);
}
