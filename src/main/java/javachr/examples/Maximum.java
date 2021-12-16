package javachr.examples;

import javachr.RuleApplicator;
import javachr.SimpleRuleApplicator;
import javachr.constraints.Constraint;
import javachr.rules.Rule;
import javachr.rules.Simpagation;

import java.util.List;
import java.util.Random;

import static javachr.examples.ExampleFactory.printDurationAndResult;

public class Maximum {
    public static void main(String[] args) {
        Random ran = new Random();
        Integer[] array = new Integer[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = ran.nextInt(1000);
        }
        array[0] = 0;

        Simpagation rule = new Simpagation(1, 1)
                .guard((h1, h2) -> ((int) h1[0].get()) >= ((int) h2[0].get()));

        List<Constraint<?>> result;

        RuleApplicator solver = new SimpleRuleApplicator(rule);
//        solver.setTracer(new CommandLineTracer(true));

        long start = System.currentTimeMillis();
        result = solver.execute(array);
        long end = System.currentTimeMillis();
        printDurationAndResult(start, end, result);
    }

    static Rule[] getRules(){
        return new Rule[]{
                new Simpagation(1, 1)
                .guard((h1, h2) -> (int) h1[0].get() >= (int) h2[0].get())
        };
    }
}
