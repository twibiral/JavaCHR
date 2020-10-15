package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintHandler;
import wibiral.tim.javachr.SimpleHandler;

public class ExampleFactory {
    public enum EXAMPLE {
        COLOR_MIXING, FIBONACCI, FIB_FAST, GREATEST_COMMON_DIVISOR, GCD_FAST, MAX;
    }

    public ConstraintHandler get(EXAMPLE name){
        switch (name){
            case COLOR_MIXING:
                return new SimpleHandler(ColorMixing.getRules());

            case FIBONACCI:
                return new SimpleHandler(Fibonacci.getRules());

            case FIB_FAST:
                return new SimpleHandler(FastFibonacci.getRules());

            case GREATEST_COMMON_DIVISOR:
                return new SimpleHandler(GreatestCommonDivisor.getRules());

            case GCD_FAST:
                return new SimpleHandler(FastGCD.getRules());

            case MAX:
                return new SimpleHandler(Maximum.getRules());
        }

        return null;
    }
}
