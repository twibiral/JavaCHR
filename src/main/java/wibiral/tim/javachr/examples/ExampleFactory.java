package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.RuleApplicator;
import wibiral.tim.javachr.SimpleRuleApplicator;


/**
 * Use this class to get {@link SimpleRuleApplicator} instances. They were instantiated with some
 * example rules. Try passing some Constraints to the {@link SimpleRuleApplicator#solve(Object[])} method.
 */
public class ExampleFactory {
    public enum EXAMPLE {
        COLOR_MIXING, FIBONACCI, FIB_FAST, GREATEST_COMMON_DIVISOR, GCD_FAST, MAX, REMOVE_DUPLICATES
    }

    public static RuleApplicator get(EXAMPLE name){
        switch (name){
            case COLOR_MIXING:
                return new SimpleRuleApplicator(ColorMixing.getRules());

            case FIBONACCI:
                return new SimpleRuleApplicator(Fibonacci.getRules());

            case FIB_FAST:
                return new SimpleRuleApplicator(FastFibonacci.getRules());

            case GREATEST_COMMON_DIVISOR:
                return new SimpleRuleApplicator(GreatestCommonDivisor.getRules());

            case GCD_FAST:
                return new SimpleRuleApplicator(FastGCD.getRules());

            case MAX:
                return new SimpleRuleApplicator(Maximum.getRules());

            case REMOVE_DUPLICATES:
                return new SimpleRuleApplicator(RemoveDuplicates.getRules());
        }

        return null;
    }
}
