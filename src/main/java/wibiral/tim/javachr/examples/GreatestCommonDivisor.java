package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.*;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Simpagation;
import wibiral.tim.javachr.rules.Simplification;

public class GreatestCommonDivisor {
    public static void main(String[] args) {
        // Define rules to find the greatest common divisor:
        RuleSet ruleSet = new RuleSet();
        ruleSet.add(new Simpagation(1, 1).guard(
                (h1, h2) -> h1[0].value() instanceof Integer && h2[0].value() instanceof Integer &&
                        (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value()
        ).body(
                (x1, x2, newConstraints) -> {
                    int n = (int) x1[0].value();
                    int m = (int) x2[0].value();
                    newConstraints.add(new Constraint<>(m - n));
                }
        ));
        ruleSet.add(new Simplification(1).guard(
                x -> x[0].value() instanceof Integer && (int) x[0].value() == 0
                ).body(
                (x,y) -> {  }
                )
        );

        // SimpleSolver is a naive implementation to apply some rules to some Constraints:
        ConstraintSolver solver = new SimpleSolver(ruleSet);
        ConstraintStore result;
        long start, end;

        System.out.println("Greatest common divisor for 27 and 9:");
        start = System.currentTimeMillis(); // Stop time and print it
        result = solver.solve(27, 9);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end-start) + "ms\n" + result + "\n");

        System.out.println("Greatest common divisor for 1337 and 42:");
        start = System.currentTimeMillis();
        result = solver.solve(1337, 42);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end-start) + "ms\n" + result + "\n");

        System.out.println("Greatest common divisor for 11, 253 and 25.751:");
        start = System.currentTimeMillis();
        result = solver.solve(11, 253, 25751);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end-start) + "ms\n" + result + "\n");

        System.out.println("Greatest common divisor for 12.312, 12.132, 112, 4234 and 1211:");
        start = System.currentTimeMillis();
        result = solver.solve(12312, 12132, 112, 4234, 1211);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end-start) + "ms\n" + result);

    }
}
