package wibiral.tim.newjavachr.examples;

import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.ConstraintSolver;
import wibiral.tim.newjavachr.SimpleConstraintSolver;
import wibiral.tim.newjavachr.rules.Head;
import wibiral.tim.newjavachr.rules.Propagation;
import wibiral.tim.newjavachr.rules.Rule;
import wibiral.tim.newjavachr.rules.Simpagation;
import wibiral.tim.newjavachr.tracing.CommandLineTracer;

import java.util.List;

import static wibiral.tim.newjavachr.rules.Head.OF_TYPE;

public class Fibonacci {
    public static class fib {
        final int a;
        final long b;

        fib(int a, long b){
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString(){
            return "fib(" + a + ", " + b + ")";
        }

        @Override
        public boolean equals(Object obj){
            return obj instanceof fib && ((fib) obj).a == this.a && ((fib) obj).b == this.b;
        }
    }

    public static void main(String[] args) {
        ConstraintSolver fibHandler = new SimpleConstraintSolver(getRules());
        fibHandler.setTracer(new CommandLineTracer(true));

        System.out.println("Fibonacci 42:");
        long start = System.currentTimeMillis();
        List<Constraint<?>> result = fibHandler.solve(10);
        long end = System.currentTimeMillis();
        System.out.println(result + "\nDuration: " + (end - start) + "ms");
    }

    static Rule[] getRules(){
//        Rule r1 = new Propagation("MAX => fib(0, 1), fib(1, 1).", 1)  // MAX => fib(0, 1), fib(1, 1).
//                .guard(x -> x[0].value() instanceof Integer)
//                .body((oldC, newC) -> {
//                    newC.add(new Constraint<>(new fib(0, 0)));
//                    newC.add(new Constraint<>(new fib(1, 1)));
//                });

//        Rule r2 = new Propagation("MAX, fib(N1, M1), fib(N2, M2) =>  N1 == N2 - 1 | fib(N2 + 1, M1 + M2).", 3)
//                // MAX, fib(N1, M1), fib(N2, M2) =>  N1 == N2 - 1 | fib(N2 + 1, M1 + M2).
//                .guard(x -> x[0].value() instanceof Integer && x[1].value() instanceof fib && x[2].value() instanceof fib
//                        && ((fib) x[1].value()).a == ((fib) x[2].value()).a - 1
//                        && ((fib) x[2].value()).a < (int) x[0].value()
//                ).body((oldC, newC) -> {
//                    int N2 = ((fib) oldC[2].value()).a;
//                    long M1 = ((fib) oldC[1].value()).b;
//                    long M2 = ((fib) oldC[2].value()).b;
//                    newC.add(new Constraint<>(new fib(N2+1, M1 + M2)));
//                });

//        Rule r3 = new Simpagation(1, 1)
//                .guard((h1, h2) -> h1[0].value() instanceof fib && h2[0].value() instanceof Integer
//                        && h2[0].equals(((fib) h1[0].value()).a));

        Rule r1 = new Propagation(OF_TYPE(Integer.class))  // MAX => fib(0, 1), fib(1, 1).
//                .guard(x -> x[0].value() instanceof Integer)
                .body((oldC, newC) -> {
                    newC.add(new Constraint<>(new fib(0, 0)));
                    newC.add(new Constraint<>(new fib(1, 1)));
                });
//
        Rule r2 = new Propagation(OF_TYPE(Integer.class), OF_TYPE(fib.class), OF_TYPE(fib.class))
                // MAX, fib(N1, M1), fib(N2, M2) =>  N1 == N2 - 1 | fib(N2 + 1, M1 + M2).
                .guard(x ->{
                            System.out.println("Guard: " + x[0] + "  " + x[1] + "  " + x[2]);
//                        x[0].value() instanceof Integer && x[1].value() instanceof fib && x[2].value() instanceof fib &&
                                return ((fib) x[1].value()).a == ((fib) x[2].value()).a - 1
                                && ((fib) x[2].value()).a < (int) x[0].value();
                        }
                ).body((oldC, newC) -> {
                    int N2 = ((fib) oldC[2].value()).a;
                    long M1 = ((fib) oldC[1].value()).b;
                    long M2 = ((fib) oldC[2].value()).b;
                    newC.add(new Constraint<>(new fib(N2+1, M1 + M2)));
                });
//
        Rule r3 = new Simpagation(1, 1)
                .guard((h1, h2) -> h1[0].value() instanceof fib && h2[0].value() instanceof Integer
                        && h2[0].equals(((fib) h1[0].value()).a));

        return new Rule[]{r1, r2, r3};
    }
}
