package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.ConstraintSolver;
import wibiral.tim.javachr.SimpleConstraintSolver;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simpagation;
import wibiral.tim.javachr.tracing.CommandLineTracer;

import java.util.List;

import static wibiral.tim.javachr.rules.head.Head.ofType;

public class Fibonacci {
    public static class Fib {
        final int a;
        final long b;

        Fib(int a, long b){
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString(){
            return "fib(" + a + ", " + b + ")";
        }

        @Override
        public boolean equals(Object obj){
            return obj instanceof Fib && ((Fib) obj).a == this.a && ((Fib) obj).b == this.b;
        }
    }

    public static void main(String[] args) {
        ConstraintSolver fibHandler = new SimpleConstraintSolver(getRules());
        fibHandler.setTracer(new CommandLineTracer());

        System.out.println("Fibonacci 42:");
        long start = System.currentTimeMillis();
        List<Constraint<?>> result = fibHandler.solve(42);
        long end = System.currentTimeMillis();
        System.out.println(result + "\nDuration: " + (end - start) + "ms");
    }

    static Rule[] getRules(){
        // MAX => fib(0, 1), fib(1, 1).
        Rule r1 = new Propagation(ofType(Integer.class))
//                .guard(x -> {})   // Not necessary
                .body((head, newConstraints) -> {
                    newConstraints.add(new Constraint<>(new Fib(0, 0)));
                    newConstraints.add(new Constraint<>(new Fib(1, 1)));
                });

        // MAX, fib(N1, M1), fib(N2, M2) =>  N1 == N2 - 1 | fib(N2 + 1, M1 + M2).
        Rule r2 = new Propagation(ofType(Integer.class), ofType(Fib.class), ofType(Fib.class))
                .guard(head ->
                            ((Fib) head[1].get()).a == ((Fib) head[2].get()).a - 1
                            && ((Fib) head[2].get()).a < (int) head[0].get()
                ).body((head, newConstraints) -> {
                    int N2 = ((Fib) head[2].get()).a;
                    long M1 = ((Fib) head[1].get()).b;
                    long M2 = ((Fib) head[2].get()).b;
                    newConstraints.add(new Constraint<>(new Fib(N2+1, M1 + M2)));
                });
//
        Rule r3 = new Simpagation(1, ofType(Fib.class), ofType(Integer.class))
                .guard((h1, h2) -> h2[0].get().equals(((Fib) h1[0].get()).a));

        return new Rule[]{r1, r2, r3};
    }

    public static Rule[] getRules2(){
        // MAX => fib(0, 1), fib(1, 1).
        Rule r1 = new Propagation(Integer.class)
//                .guard(head -> {})    // Not necessary
                .body((head, newConstraints) -> {
                    newConstraints.add(new Constraint<>(new Fib(0, 0)));
                    newConstraints.add(new Constraint<>(new Fib(1, 1)));
                });

        // MAX, fib(N1, M1), fib(N2, M2) =>  N1 == N2 - 1 | fib(N2 + 1, M1 + M2).
        Rule r2 = new Propagation(Integer.class, Fib.class, Fib.class)
                .guard(x ->
                        ((Fib) x[1].get()).a == ((Fib) x[2].get()).a - 1
                        && ((Fib) x[2].get()).a < (int) x[0].get()
                )
                .body((head, newConstraints) -> {
                    int N2 = ((Fib) head[2].get()).a;
                    long M1 = ((Fib) head[1].get()).b;
                    long M2 = ((Fib) head[2].get()).b;
                    newConstraints.add(new Constraint<>(new Fib(N2+1, M1 + M2)));
                });

        Rule r3 = new Simpagation(1, 1)
                .guard((h1, h2) ->
                        h1[0].get() instanceof Fib && h2[0].get() instanceof Integer
                                && h2[0].get().equals(((Fib) h1[0].get()).a)
                );

        return new Rule[]{r1, r2, r3};
    }

    public static Rule[] getRules3(){
        // MAX => fib(0, 1), fib(1, 1).
        Rule r1 = new Propagation(1)
                .guard(head ->
                        head[0].get() instanceof Integer
                )
                .body((head, newConstraints) -> {
                    newConstraints.add(new Constraint<>(new Fib(0, 0)));
                    newConstraints.add(new Constraint<>(new Fib(1, 1)));
                });

        // MAX, fib(N1, M1), fib(N2, M2) =>  N1 == N2 - 1 | fib(N2 + 1, M1 + M2).
        Rule r2 = new Propagation(3)
                .guard(head ->
                        head[0].get() instanceof Integer && head[1].get() instanceof Fib && head[2].get() instanceof Fib
                        && ((Fib) head[1].get()).a == ((Fib) head[2].get()).a - 1
                        && ((Fib) head[2].get()).a < (int) head[0].get()
                )
                .body((head, newConstraints) -> {
                    int N2 = ((Fib) head[2].get()).a;
                    long M1 = ((Fib) head[1].get()).b;
                    long M2 = ((Fib) head[2].get()).b;
                    newConstraints.add(new Constraint<>(new Fib(N2+1, M1 + M2)));
                });

        Rule r3 = new Simpagation(1, 1)
                .guard((h1, h2) ->
                        h1[0].get() instanceof Fib && h2[0].get() instanceof Integer
                        && h2[0].get().equals(((Fib) h1[0].get()).a)
                );

        return new Rule[]{r1, r2, r3};
    }
}
