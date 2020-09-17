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
        Integer[] array = new Integer[75000];
        for (int i = 0; i < array.length; i++) {
            array[i] = ran.nextInt(1000);
        }

        Simpagation rule = new Simpagation(1, 1)
                .guard((h1, h2) -> (Integer) h1[0].value() >= (Integer) h2[0].value());

        ConstraintStore result;
        long start, end;

        // Sequential approach:
        ConstraintHandler handler = new SimpleHandler(rule);
        start = System.currentTimeMillis();
        result = handler.solve(array);
        end = System.currentTimeMillis();
        System.out.println("Sequential computed: " + result.toString() + "\nTime: " + (end - start) + "ms\n");


        // Semi-parallel approach:
        SemiParallelHandler parallelHandler = new SemiParallelHandler(8, rule);
        start = System.currentTimeMillis();
        result = parallelHandler.solve(array);
        end = System.currentTimeMillis();
        System.out.println("Parallel computed: " + result.toString() + "\nTime: " + (end - start) + "ms\n");
        parallelHandler.kill();
    }
}
