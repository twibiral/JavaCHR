package wibiral.tim.newjavachr.examples;

import wibiral.tim.newjavachr.rules.Simpagation;
import wibiral.tim.newjavachr.ConstraintSolver;
import wibiral.tim.newjavachr.SimpleConstraintSolver;
import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.rules.Rule;
import wibiral.tim.newjavachr.rules.Simplification;

import java.util.List;

public class GreatestCommonDivisor {
    public static void main(String[] args) {
        ConstraintSolver gcdHandler = new SimpleConstraintSolver(getRules());
//        gcdHandler.setTracer(new CommandLineTracer(true));

        List<Constraint<?>> result;
        long start, end;

        System.out.println("Greatest common divisor for 27 and 9:");
        start = System.currentTimeMillis(); // Stop time and print it
        result = gcdHandler.solve(27, 9);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end - start) + "ms\n" + result + "\n");


        System.out.println("Greatest common divisor for 1337 and 42:");
        start = System.currentTimeMillis();
        result = gcdHandler.solve(1337, 42);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end - start) + "ms\n" + result + "\n");


        System.out.println("Greatest common divisor for 11, 253 and 25.751:");
        start = System.currentTimeMillis();
        result = gcdHandler.solve(11, 253, 25751);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end - start) + "ms\n" + result + "\n");


        System.out.println("Greatest common divisor for 12.312, 12.132, 112, 4234 and 1211:");
        start = System.currentTimeMillis();
        result = gcdHandler.solve(12312, 12132, 112, 4234, 1211);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end - start) + "ms\n" + result + "\n");


        System.out.println("Greatest common divisor for 9897392, 2837812, 1211, 283749:");
        start = System.currentTimeMillis();
        result = gcdHandler.solve(9897392, 2837812, 1211, 283749);
        end = System.currentTimeMillis();
        System.out.println("Duration: " + (end - start) + "ms\n" + result + "\n");

    }

    static Rule[] getRules(){
        // X1 / X2 <=> X1>0, X1=<X2 | int(X2-X1).
        Rule r1 = new Simpagation(1, 1)
                .guard(
                        (h1, h2) ->
                                // h1[0].value() instanceof Integer && h2[0].value() instanceof Integer &&
                                // Not necessary if you can be sure that all Constraints are Integers.
                                (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value()
                ).body(
                        (x1, x2, newConstraints) -> {
                            int n = (int) x1[0].value();
                            int m = (int) x2[0].value();
                            newConstraints.add(new Constraint<>(m - n));
                        }
                );

        // X1, X2 <=> X1>0, X1=<X2 | int(X1), int(X2-X1)
//        Rule r1 = new Simplification(2)
//                .guard(
//                        (head) -> (int) head[0].value() > 0 && (int) head[0].value() <= (int) head[1].value()
//                ).body(
//                        (head, newConstraints) -> {
//                            int n = (int) head[0].value();
//                            int m = (int) head[1].value();
//                            newConstraints.add(new Constraint<>(n));
//                            newConstraints.add(new Constraint<>(m - n));
//                        }
//                );

        // X <=> X=0 | true.
        Rule r2 = new Simplification(1)
                .guard(
                        x ->{
                            // Not necessary if you can be sure that all Constraints are Integers.
                            // x[0].value() instanceof Integer &&
                            return (int) x[0].value() == 0;
                        }
                ).body( (x, y) -> {} );

        return new Rule[]{r1, r2};
    }
}
