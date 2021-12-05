package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintSolver;
import wibiral.tim.javachr.SimpleConstraintSolver;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.examples.Fibonacci.Fib;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simplification;

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
        Rule r1 = new Propagation(1)  // MAX => Fib(0, 1), Fib(1, 1).
                .guard(x -> x[0].value() instanceof Integer)
                .body((head, newConstraints) -> {
                    newConstraints.add(new Constraint<>(new Fib(0, 0)));
                    newConstraints.add(new Constraint<>(new Fib(1, 1)));
                });

        // Combines:
        // MAX, Fib(N+1, X) / Fib(N, Y) <=> Fib(N+2, X+Y).
        // MAX, Fib(N+1, X) Fib(N, Y) <=> MAX == N+1 | Fib(N+1, X).
        Rule r2 = new Simplification(3)
                .guard(x -> x[0].value() instanceof Integer
                            && !(x[1].value() instanceof Integer) //&& !(x[2].value() instanceof Integer)
                            && ((Fib) x[1].value()).a + 1 == ((Fib) x[2].value()).a)
                .body((head, newConstraints) -> {
                    // Fib n and Fib n+1:
                    Fib n1 = (Fib) head[1].value();
                    Fib n2 = (Fib) head[2].value();
                    // Calculate Fib n+2:
                    Constraint<Fib> newFib = new Constraint<>(new Fib(n2.a + 1, n1.b + n2.b));

                    // Add Fib n+1 and Fib n+2
                    newConstraints.add(newFib);

                    if(n2.a + 1 < (int) head[0].value()) {
                        // Add max and Fib n+1 just when max not reached now
                        newConstraints.add(head[2]);
                        newConstraints.add(head[0]);
                    }
                });

        return new Rule[]{r1, r2};
    }
}
