package wibiral.tim.newjavachr.examples;

import wibiral.tim.newjavachr.rules.head.Head;
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
        start = System.nanoTime(); // Stop time and print it
        result = gcdHandler.solve(27, 9);
        end = System.nanoTime();
        System.out.println("Duration: " + (end - start)/1000000.0 + "ms\n" + result + "\n");


        System.out.println("Greatest common divisor for 1337 and 42:");
        start = System.nanoTime();
        result = gcdHandler.solve(1337, 42);
        end = System.nanoTime();
        System.out.println("Duration: " + (end - start)/1000000.0 + "ms\n" + result + "\n");


        System.out.println("Greatest common divisor for 11, 253 and 25.751:");
        start = System.nanoTime();
        result = gcdHandler.solve(11, 253, 25751);
        end = System.nanoTime();
        System.out.println("Duration: " + (end - start)/1000000.0 + "ms\n" + result + "\n");


        System.out.println("Greatest common divisor for 12.312, 12.132, 112, 4234 and 1211:");
        start = System.nanoTime();
        result = gcdHandler.solve(12312, 12132, 112, 4234, 1211);
        end = System.nanoTime();
        System.out.println("Duration: " + (end - start)/1000000.0 + "ms\n" + result + "\n");


        System.out.println("Greatest common divisor for 9897392, 2837812, 1211, 283749:");
        start = System.nanoTime();
        result = gcdHandler.solve(9897392, 2837812, 1211, 283749);
        end = System.nanoTime();
        System.out.println("Duration: " + (end - start)/1000000.0 + "ms\n" + result + "\n");

    }

    /**
     * @return Rules defined by defining every Constraint in the head.
     */
    public static Rule[] getRules(){
        // X1 / X2 <=> X1>0, X1=<X2 | int(X2-X1).
        Rule r1 = new Simpagation(1, Head.OF_TYPE(Integer.class), Head.OF_TYPE(Integer.class))
                .guard(
                        (h1, h2) -> (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value()
                ).body(
                        (h1, h2, newConstraints) -> {
                            int n = (int) h1[0].value();
                            int m = (int) h2[0].value();
                            newConstraints.add(new Constraint<>(m - n));
                        }
                );

        // X <=> X=0 | true.
        Rule r2 = new Simplification("X <=> X=0 | true.", Head.OF_VALUE(0));
//                  .guard(head ->{ })    // not necessary
//                  .body( (head, newConstraints) -> {} );    // not necessary

        return new Rule[]{r1, r2};
    }

    /**
     * @return Rules defined by defining just the types of the constraints in the head.
     */
    public static Rule[] getRules2(){
        // X1 / X2 <=> X1>0, X1=<X2 | int(X2-X1).
        Rule r1 = new Simpagation(1, Integer.class, Integer.class)
                .guard(
                        (h1, h2) -> (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value()
                ).body(
                        (h1, h2, newConstraints) -> {
                            int n = (int) h1[0].value();
                            int m = (int) h2[0].value();
                            newConstraints.add(new Constraint<>(m - n));
                        }
                );

        // X <=> X=0 | true.
        Rule r2 = new Simplification("X <=> X=0 | true.", Integer.class)
                .guard(head ->{
                            // The solver guarantees the type of the constraint to be integer
                            return (int) head[0].value() == 0;
                        });
//                  .body( (head, newConstraints) -> {} );    // not necessary

        return new Rule[]{r1, r2};
    }

    /**
     * @return Rule with just the size of head defined in the rule definition.
     */
    public static Rule[] getRules3(){
        // X1 / X2 <=> X1>0, X1=<X2 | int(X2-X1).
        Rule r1 = new Simpagation(1, 1)
                .guard(
                        (h1, h2) ->
                                // Type check necessary if you can be sure that all Constraints are Integers.
                                h1[0].value() instanceof Integer && h2[0].value() instanceof Integer &&
                                (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value()
                ).body(
                        (x1, x2, newConstraints) -> {
                            int n = (int) x1[0].value();
                            int m = (int) x2[0].value();
                            newConstraints.add(new Constraint<>(m - n));
                        }
                );

        // X <=> X=0 | true.
        Rule r2 = new Simplification(1)
                .guard(
                        head -> head[0].value() instanceof  Integer && (int) head[0].value() == 0
                );
//                  .body( (head, newConstraints) -> {} );    // not necessary


        return new Rule[]{r1, r2};
    }
}
