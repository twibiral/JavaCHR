package javachr.examples;

import javachr.RuleApplicator;
import javachr.SimpleRuleApplicator;
import javachr.constraints.Constraint;
import javachr.examples.Fibonacci.Fib;
import javachr.rules.Propagation;
import javachr.rules.Rule;
import javachr.rules.Simplification;
import javachr.rules.head.Head;

import java.util.List;

import static javachr.examples.ExampleFactory.printDurationAndResult;

public class FastFibonacci {
    public static void main(String[] args) {
        RuleApplicator fastFibSolver = new SimpleRuleApplicator(getRules());
//        fastFibSolver.setTracer(new CommandLineTracer());

        System.out.println("Fibonacci 42:");
        long start = System.nanoTime();
        List<Constraint<?>> result = fastFibSolver.execute( 42);   // max before overflow (negative number): 92
        long end = System.nanoTime();
        printDurationAndResult(start, end, result);

    }

    static Rule[] getRules(){
        Rule r1 = new Propagation(Head.ofType(Integer.class))  // MAX => Fib(0, 1), Fib(1, 1).
                .body((head, newConstraints) -> {
                    newConstraints.add(new Constraint<>(new Fib(0, 0)));
                    newConstraints.add(new Constraint<>(new Fib(1, 1)));
                });

        // Combines:
        // MAX, Fib(N+1, X) / Fib(N, Y) <=> Fib(N+2, X+Y).
        // MAX, Fib(N+1, X) Fib(N, Y) <=> MAX == N+1 | Fib(N+1, X).
        Rule r2 = new Simplification(Head.ofType(Integer.class), Head.ofType(Fib.class), Head.ofType(Fib.class))
                .guard(head -> ((Fib) head[1].get()).a + 1 == ((Fib) head[2].get()).a)
                .body((head, newConstraints) -> {
                    // Fib n and Fib n+1:
                    Fib n1 = (Fib) head[1].get();
                    Fib n2 = (Fib) head[2].get();
                    // Calculate Fib n+2:
                    Constraint<Fib> newFib = new Constraint<>(new Fib(n2.a + 1, n1.b + n2.b));

                    // Add Fib n+1 and Fib n+2
                    newConstraints.add(newFib);

                    if(n2.a + 1 < (int) head[0].get()) {
                        // Add max and Fib n+1 just when max not reached now
                        newConstraints.add(head[2]);
                        newConstraints.add(head[0]);
                    }
                });

        return new Rule[]{r1, r2};
    }

    static Rule[] getRules2(){
        Rule r1 = new Propagation(1)  // MAX => Fib(0, 1), Fib(1, 1).
                .guard(head -> head[0].get() instanceof Integer)
                .body((head, newConstraints) -> {
                    newConstraints.add(new Constraint<>(new Fib(0, 0)));
                    newConstraints.add(new Constraint<>(new Fib(1, 1)));
                });

        // Combines:
        // MAX, Fib(N+1, X) / Fib(N, Y) <=> Fib(N+2, X+Y).
        // MAX, Fib(N+1, X) Fib(N, Y) <=> MAX == N+1 | Fib(N+1, X).
        Rule r2 = new Simplification(3)
                .guard(head -> head[0].get() instanceof Integer
                            && head[1].get() instanceof Fib && head[2].get() instanceof Fib
                            && ((Fib) head[1].get()).a + 1 == ((Fib) head[2].get()).a)
                .body((head, newConstraints) -> {
                    // Fib n and Fib n+1:
                    Fib n1 = (Fib) head[1].get();
                    Fib n2 = (Fib) head[2].get();
                    // Calculate Fib n+2:
                    Constraint<Fib> newFib = new Constraint<>(new Fib(n2.a + 1, n1.b + n2.b));

                    // Add Fib n+1 and Fib n+2
                    newConstraints.add(newFib);

                    if(n2.a + 1 < (int) head[0].get()) {
                        // Add max and Fib n+1 just when max not reached now
                        newConstraints.add(head[2]);
                        newConstraints.add(head[0]);
                    }
                });

        return new Rule[]{r1, r2};
    }
}
