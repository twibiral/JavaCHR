package wibiral.tim.javachr.examples;

import wibiral.tim.javachr.ConstraintHandler;
import wibiral.tim.javachr.RuleSet;
import wibiral.tim.javachr.SimpleHandler;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Simpagation;

public class Maximum {
    public static void main(String[] args) {
        Simpagation rule = new Simpagation(1, 1)
                .guard((h1, h2) -> (int) h1[0].value() >= (int) h2[0].value());

        ConstraintHandler handler = new SimpleHandler(rule);
        System.out.println(handler.solve(1, 2, 3, 5));
    }
}
