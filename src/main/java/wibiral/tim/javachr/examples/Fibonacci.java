package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintHandler;
import wibiral.tim.javachr.RuleSet;
import wibiral.tim.javachr.SimpleHandler;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Simpagation;

public class Fibonacci {

    static class fib {
        final int a;
        final int b;

        fib(int a, int b){
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
        RuleSet fibRules = new RuleSet();
        fibRules.add(new Propagation(1)  // MAX => fib(0, 1), fib(1, 1).
                .guard(x -> x[0].value() instanceof Integer)
                .body((oldC, newC) -> {
                    newC.add(new Constraint<>(new fib(0, 1)));
                    newC.add(new Constraint<>(new fib(1, 1)));
                })
        );

        fibRules.add(new Propagation(3)
                // MAX, fib(N1, M1), fib(N2, M2) =>  N1 == N2 - 1 | fib(N2 + 1, M1 + M2).
                .guard(x -> x[0].value() instanceof Integer && x[1].value() instanceof fib && x[2].value() instanceof fib
                            && ((fib) x[1].value()).a == ((fib) x[2].value()).a - 1
                            && ((fib) x[2].value()).a < (int) x[0].value()
                ).body((oldC, newC) -> {
                    int N2 = ((fib) oldC[2].value()).a;
                    int M1 = ((fib) oldC[1].value()).b;
                    int M2 = ((fib) oldC[2].value()).b;
                    newC.add(new Constraint<>(new fib(N2+1, M1 + M2)));
                })
        );

        fibRules.add(new Simpagation(1, 1)
                .guard((h1, h2) -> h1[0].value() instanceof fib && h2[0].value() instanceof Integer
                        && h2[0].equals(((fib) h1[0].value()).a)));

        ConstraintHandler fibonacci = new SimpleHandler(fibRules);

        System.out.println("Fibonacci 42:");
        long start = System.currentTimeMillis();
        System.out.println(fibonacci.solve(42));
        long end = System.currentTimeMillis();
        System.out.println("Duration: " + (end - start) + "ms");
    }
}