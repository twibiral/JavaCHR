package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.Constraint;
import wibiral.tim.javachr.ConstraintStore;
import wibiral.tim.javachr.RuleSet;
import wibiral.tim.javachr.SimpleSolver;
import wibiral.tim.javachr.rules.Simpagation;
import wibiral.tim.javachr.rules.Simplification;

public class FirstTest {
    public static void main(String[] args) {
        System.out.println("Starting...");

        RuleSet rules = new RuleSet();
//        rules.add(new Simplification(1)
//                                    .guard(x -> x[0].value() instanceof Integer
//                                                   //&& (int) x[0].value() > 1
//                                            // && (int) x[0].value() % 5 == 0
//                                            // && (int) x[1].value() % 10 == 0
//                                    )
//                                    .body((c, nc) -> {
//                                        nc.add(new Constraint<>("Hello World!"));
//                                    })
//                                    );
//        rules.add(new Simpagation(1, 1)
//                        .guard((h1, h2) -> h1[0].value() instanceof Integer && h2[0].value() instanceof Integer
//                            && (int) h1[0].value() == 25 && (int) h2[0].value() == 5)
//                        .body((c1, c2, nc) -> {
//                            System.out.println("Rule applied");
//                            nc.add(new Constraint<>("Hello World!"));
//                            }));

        rules = getGreatestCommonDivisorRules();
        SimpleSolver constraintSolver = new SimpleSolver(rules);

        long start = System.currentTimeMillis();
        ConstraintStore store = constraintSolver.solve(new ConstraintStore(1337, 42));
        long end = System.currentTimeMillis();

        System.out.println("Duration: " + (end-start) + "ms");
        System.out.println(store);
    }

    static RuleSet getGreatestCommonDivisorRules(){
        RuleSet ruleSet = new RuleSet();
        ruleSet.add(new Simpagation(1, 1).guard(
                    (h1, h2) -> h1[0].value() instanceof Integer && h2[0].value() instanceof Integer &&
                                (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value()
                ).body(
                (x1, x2, newConstraints) -> {
                    int n = (int) x1[0].value();
                    int m = (int) x2[0].value();
                    newConstraints.add(new Constraint<>(m - n));
                }
                ));
        ruleSet.add(new Simplification(1).guard(
                x -> x[0].value() instanceof Integer && (int) x[0].value() == 0
                ).body(
                (x,y) -> {  }
                )
        );

        return ruleSet;
    }
}
