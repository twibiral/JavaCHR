package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintSolver;
import wibiral.tim.javachr.RuleSet;
import wibiral.tim.javachr.SimpleSolver;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Simpagation;

public class Maximum {
    public static void main(String[] args) {
        RuleSet rules = new RuleSet();
        rules.add(new Simpagation(1, 1)
                .guard((h1, h2) -> (int) h1[0].value() >= (int) h2[0].value()));
        ConstraintSolver solver = new SimpleSolver(rules);
        ConstraintStore result = solver.solve(1, 2, 3, 5);
        System.out.println(result);
    }
}
