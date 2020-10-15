package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintHandler;
import wibiral.tim.javachr.SimpleHandler;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simplification;

import wibiral.tim.javachr.examples.Fibonacci.fib;

public class FastFibonacci {
    public static void main(String[] args) {
        ConstraintHandler fastFibHandler = new SimpleHandler(getRules());

        System.out.println("Fibonacci 42:");
        long start = System.currentTimeMillis();
        System.out.println(fastFibHandler.solve(42));   // max before overflow (negative number): 92
        long end = System.currentTimeMillis();
        System.out.println("Duration: " + (end - start) + "ms");
    }

    public static Rule[] getRules(){
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
