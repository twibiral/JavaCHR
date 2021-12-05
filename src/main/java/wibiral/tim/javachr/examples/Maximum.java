package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintSolver;
import wibiral.tim.javachr.SimpleConstraintSolver;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simpagation;
import wibiral.tim.javachr.tracing.CommandLineTracer;

import java.util.List;
import java.util.Random;

public class Maximum {
    public static void main(String[] args) {
        Random ran = new Random();
        Integer[] array = new Integer[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = ran.nextInt(1000);
        }
        array[0] = 0;

        Simpagation rule = new Simpagation(1, 1)
                .guard((h1, h2) -> ((int) h1[0].value()) >= ((int) h2[0].value()));

        List<Constraint<?>> result;
        long start;
        long end;

        // Sequential approach:
        ConstraintSolver solver = new SimpleConstraintSolver(rule);
//        solver.setTracer(new CommandLineTracer(true));

        start = System.currentTimeMillis();
        result = solver.solve(array);
        end = System.currentTimeMillis();
        System.out.println("Sequential computed: " + result.toString() + "\nTime: " + (end - start) + "ms\n");


        // Semi-parallel approach:
//        SemiParallelSolver parallelSolver = new SemiParallelHandler(2, rule);
//        start = System.currentTimeMillis();
//        result = parallelSolver.solve(array);
//        end = System.currentTimeMillis();
//        System.out.println("Parallel computed: " + result.toString() + "\nTime: " + (end - start) + "ms\n");
//        parallelSolver.kill();
    }

    static Rule[] getRules(){
        return new Rule[]{
                new Simpagation(1, 1)
                .guard((h1, h2) -> (int) h1[0].value() >= (int) h2[0].value())
        };
    }
}
