package wibiral.tim.newjavachr.examples;

import wibiral.tim.newjavachr.ConstraintSolver;
import wibiral.tim.newjavachr.SimpleConstraintSolver;


/**
 * Use this class to get {@link SimpleConstraintSolver} instances. They were instantiated with some
 * example rules. Try passing some Constraints to the {@link SimpleConstraintSolver#solve(Object[])} method.
 */
public class ExampleFactory {
    public enum EXAMPLE {
        COLOR_MIXING, FIBONACCI, FIB_FAST, GREATEST_COMMON_DIVISOR, GCD_FAST, MAX, REMOVE_DUPLICATES
    }

    public static ConstraintSolver get(EXAMPLE name){
        switch (name){
            case COLOR_MIXING:
                return new SimpleConstraintSolver(ColorMixing.getRules());

            case FIBONACCI:
                return new SimpleConstraintSolver(Fibonacci.getRules());

            case FIB_FAST:
                return new SimpleConstraintSolver(FastFibonacci.getRules());

            case GREATEST_COMMON_DIVISOR:
                return new SimpleConstraintSolver(GreatestCommonDivisor.getRules());

            case GCD_FAST:
                return new SimpleConstraintSolver(FastGCD.getRules());

            case MAX:
                return new SimpleConstraintSolver(Maximum.getRules());

            case REMOVE_DUPLICATES:
                return new SimpleConstraintSolver(RemoveDuplicates.getRules());
        }

        return null;
    }
}
