package wibiral.tim.newjavachr.examples;

import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.ConstraintSolver;
import wibiral.tim.newjavachr.SimpleConstraintSolver;
import wibiral.tim.newjavachr.rules.Rule;
import wibiral.tim.newjavachr.rules.Simpagation;
import wibiral.tim.newjavachr.rules.head.Head;
import wibiral.tim.newjavachr.rules.head.VAR;
import wibiral.tim.newjavachr.tracing.CommandLineTracer;

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
        Rule r = new Simpagation("Remove duplicate", 1, Head.ANY().bindTo(VAR.X), Head.ANY().bindTo(VAR.X));
        return new Rule[]{r};
    }
}
