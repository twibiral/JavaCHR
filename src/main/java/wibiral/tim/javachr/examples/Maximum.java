package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintHandler;
import wibiral.tim.javachr.SemiParallelHandler;
import wibiral.tim.javachr.SimpleHandler;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Simpagation;

import java.util.Random;

public class Maximum {
    public static void main(String[] args) {
        Random ran = new Random();
        Integer[] array = new Integer[10];
        for (int i = 0; i < array.length; i++) {
            array[i] = ran.nextInt(1000);
        }


        // Sequential approach:
        Simpagation rule = new Simpagation(1, 1)
                .guard((h1, h2) -> (Integer) h1[0].value() >= (Integer) h2[0].value());

        ConstraintHandler handler = new SimpleHandler(rule);
        ConstraintStore result = handler.solve(array);
        System.out.println("Sequential computed: " + result.toString() + "\n");


        // Semi-parallel approach:
        SemiParallelHandler parallelHandler = new SemiParallelHandler(8, rule);
        ConstraintStore result2 = parallelHandler.solve(array);
//        result2 = parallelHandler.solve(array);
        System.out.println("Parallel computed: " + result2.toString());
        parallelHandler.kill();
    }
}
