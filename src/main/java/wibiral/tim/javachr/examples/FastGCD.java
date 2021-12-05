package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintSolver;
import wibiral.tim.javachr.SimpleConstraintSolver;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simpagation;
import wibiral.tim.javachr.rules.Simplification;

import java.util.List;

/**
 * Faster implementation of the Greatest Common Divisor that uses modulo instead of subtraction.
 * (logarithmic instead of linear complexity)
 */
public class FastGCD {
    public static void main(String[] args) {
        // SimpleConstraintSolver is a naive implementation to apply some rules to some Constraints:
        ConstraintSolver gcdSolver = new SimpleConstraintSolver(getRules());
//        gcdSolver.trace();   // use to trace execution

        List<Constraint<?>> result;
        long start, end;

        System.out.println("Greatest common divisor for 27 and 9:");
        start = System.currentTimeMillis(); // Stop time and print it
        result = gcdSolver.solve(27, 9);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end-start) + "ms\n" + result + "\n");

        System.out.println("Greatest common divisor for 1337 and 42:");
        start = System.currentTimeMillis();
        result = gcdSolver.solve(1337, 42);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end-start) + "ms\n" + result + "\n");

        System.out.println("Greatest common divisor for 11, 253 and 25.751:");
        start = System.currentTimeMillis();
        result = gcdSolver.solve(11, 253, 25751);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end-start) + "ms\n" + result + "\n");

        System.out.println("Greatest common divisor for 12.312, 12.132, 112, 4234 and 1211:");
        start = System.currentTimeMillis();
        result = gcdSolver.solve(12312, 12132, 112, 4234, 1211);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end-start) + "ms\n" + result + "\n");

        System.out.println("Greatest common divisor for 9897392, 2837812, 1211, 283749:");
        start = System.currentTimeMillis();
        result = gcdSolver.solve(9897392, 2837812, 1211, 283749);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end-start) + "ms\n" + result);

    }

    static Rule[] getRules(){
        Rule r1 = new Simpagation("m % n", 1, 1).guard(
                (h1, h2) ->
                        // h1[0].value() instanceof Integer && h2[0].value() instanceof Integer &&
                        // Not necessary if you can be sure that all Constraints are Integers.
                        (int) h1[0].get() > 0 && (int) h1[0].get() <= (int) h2[0].get()
        ).body(
                (x1, x2, newConstraints) -> {
                    int n = (int) x1[0].get();
                    int m = (int) x2[0].get();
                    newConstraints.add(new Constraint<>(m % n));
                }
        );
        Rule r2  = new Simplification( "Delete zero", 1)
                .guard(x -> (int) x[0].get() == 0);

        return new Rule[]{r1, r2};
    }
}
