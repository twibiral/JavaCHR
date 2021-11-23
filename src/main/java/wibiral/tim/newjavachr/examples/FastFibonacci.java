package wibiral.tim.newjavachr.examples;

import wibiral.tim.newjavachr.ConstraintSolver;
import wibiral.tim.newjavachr.SimpleConstraintSolver;
import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.examples.Fibonacci.fib;
import wibiral.tim.newjavachr.rules.Propagation;
import wibiral.tim.newjavachr.rules.Rule;
import wibiral.tim.newjavachr.rules.Simplification;

import java.util.List;

public class FastFibonacci {
    public static void main(String[] args) {
        ConstraintSolver fastFibSolver = new SimpleConstraintSolver(getRules());

        System.out.println("Fibonacci 42:");
        long start = System.currentTimeMillis();
        List<Constraint<?>> result = fastFibSolver.solve(42);   // max before overflow (negative number): 92
        long end = System.currentTimeMillis();
        System.out.println(result);
        System.out.println("Duration: " + (end - start) + "ms");
    }

    static Rule[] getRules(){
        Rule r1 = new Propagation(1)  // MAX => fib(0, 1), fib(1, 1).
                .guard(x -> x[0].value() instanceof Integer)
                .body((oldC, newC) -> {
                    newC.add(new Constraint<>(new fib(0, 0)));
                    newC.add(new Constraint<>(new fib(1, 1)));
                });

        Rule r2 = new Simplification(3)
                .guard(x -> x[0].value() instanceof Integer
                            && !(x[1].value() instanceof Integer) //&& !(x[2].value() instanceof Integer)
                            && ((fib) x[1].value()).a + 1 == ((fib) x[2].value()).a)
                .body((oldC, newC) -> {
                    // fib n and fib n+1:
                    fib n1 = (fib) oldC[1].value();
                    fib n2 = (fib) oldC[2].value();
                    // Calculate fib n+2:
                    Constraint<fib> newFib = new Constraint<>(new fib(n2.a + 1, n1.b + n2.b));

                    // Add fib n+1 and fib n+2
                    newC.add(newFib);

                    if(n2.a + 1 < (int) oldC[0].value()) {
                        // Add max and fib n+1 just when max not reached now
                        newC.add(oldC[2]);
                        newC.add(oldC[0]);
                    }
                });

        return new Rule[]{r1, r2};
    }
}
