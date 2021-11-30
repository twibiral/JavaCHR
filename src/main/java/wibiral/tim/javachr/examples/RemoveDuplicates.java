package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.ConstraintSolver;
import wibiral.tim.javachr.SimpleConstraintSolver;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simpagation;
import wibiral.tim.javachr.rules.head.Head;
import wibiral.tim.javachr.rules.head.VAR;

import java.util.List;

public class RemoveDuplicates {
    public static void main(String[] args) {
        ConstraintSolver solver = new SimpleConstraintSolver(getRules());
//        solver.setTracer(new CommandLineTracer());

        List<Constraint<?>> result = solver.solve(1, 2, 3, 4, 1, 2, 3, 1, 2);
        System.out.println(result);

        result = solver.solve("Hallo", 42, "Welt", 1337, "Hallo");
        System.out.println(result);
    }

    public static Rule[] getRules(){
        Rule r = new Simpagation("Remove duplicate", 1, Head.any().bindTo(VAR.X), Head.any().bindTo(VAR.X));
        return new Rule[]{r};
    }
}
