package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.RuleApplicator;
import wibiral.tim.javachr.SimpleRuleApplicator;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simpagation;
import wibiral.tim.javachr.rules.head.Head;
import wibiral.tim.javachr.rules.head.VAR;

import java.util.List;

import static wibiral.tim.javachr.examples.ExampleFactory.printDurationAndResult;

public class RemoveDuplicates {
    public static void main(String[] args) {
        RuleApplicator solver = new SimpleRuleApplicator(getRules());
//        solver.setTracer(new CommandLineTracer());

        long start, end;

        start = System.nanoTime();
        List<Constraint<?>> result = solver.execute(1, 2, 3, 4, 1, 2, 3, 1, 2);
        end = System.nanoTime();
        printDurationAndResult(start, end, result);

        start = System.nanoTime();
        result = solver.execute("Hallo", 42, "Welt", 1337, "Hallo");
        end = System.nanoTime();
        printDurationAndResult(start, end, result);
    }

    public static Rule[] getRules(){
        Rule r = new Simpagation("Remove duplicate", 1, Head.any().bindTo(VAR.X), Head.any().bindTo(VAR.X));
        return new Rule[]{r};
    }
}
