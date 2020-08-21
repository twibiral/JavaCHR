package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintSolver;
import wibiral.tim.javachr.RuleSet;
import wibiral.tim.javachr.SimpleSolver;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Propagation;

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
            return obj instanceof fib && ((fib) obj).a == a && ((fib) obj).b == b;
        }
    }

    public static void main(String[] args) {
        RuleSet fibRules = new RuleSet();
        fibRules.add(new Propagation(1)
                .guard(x -> x[0].value() instanceof Integer)
                .body((oldC, newC) -> {
                    newC.add(new Constraint<>(new fib(0, 1)));
                    newC.add(new Constraint<>(new fib(1, 1)));
                }));

        fibRules.add(new Propagation(3)
                .guard(head -> {
                    if(! (head[0].value() instanceof Integer && head[1].value() instanceof fib && head[2].value() instanceof fib))
                        return false;

                    int MAX = (int) head[0].value();
                    int N1 = ((fib) head[1].value()).a;
                    int N2 = ((fib) head[2].value()).a;
                    return MAX > N2 && N2 == N1+1;
                } )
                .body((head, newC) -> {
                    int N2 = ((fib) head[2].value()).a;
                    int M1 = ((fib) head[1].value()).b;
                    int M2 = ((fib) head[2].value()).b;

                    newC.add(new Constraint<>(new fib(N2+1, M1+M2)));
                }));

        ConstraintSolver fibonacci = new SimpleSolver(fibRules);

        System.out.println("Fibonacci 6:");
        System.out.println(fibonacci.solve(6));

    }
}
