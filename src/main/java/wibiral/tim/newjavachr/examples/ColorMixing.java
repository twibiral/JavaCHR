package wibiral.tim.newjavachr.examples;

import wibiral.tim.newjavachr.ConstraintSolver;
import wibiral.tim.newjavachr.SimpleConstraintSolver;
import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.rules.Rule;
import wibiral.tim.newjavachr.rules.Simpagation;
import wibiral.tim.newjavachr.rules.Simplification;
import wibiral.tim.newjavachr.tracing.CommandLineTracer;

import java.util.ArrayList;
import java.util.List;

/**
 * CHR program that gives you the result of mixing some colors.
 * Inspired by Thom Frühwirth's example at http://www.chr.informatik.uni-ulm.de/~webchr/.
 * @see <a href="http://chr.informatik.uni-ulm.de/~webchr/">interaktive chr website</a>
 */
public class ColorMixing {
    public enum COLOR {
        RED, BLUE, YELLOW, GREEN, PURPLE, ORANGE, BROWN
    }

    public static void main(String[] args) {
        ConstraintSolver colorHandler = new SimpleConstraintSolver(getRules());
        colorHandler.setTracer(new CommandLineTracer()); // Use to see the process of the rule application

        COLOR redAndBlue = (COLOR) colorHandler.solve(COLOR.RED, COLOR.BLUE).get(0).value();
        System.out.println(colorHandler.solve(COLOR.RED, COLOR.BLUE).size());
        System.out.println("red and blue is " + redAndBlue.toString());

        COLOR redAndYellow = (COLOR) colorHandler.solve(COLOR.RED, COLOR.YELLOW).get(0).value();
        System.out.println("red and yellow is " + redAndYellow.toString());

        COLOR greenAndGreen = (COLOR) colorHandler.solve(COLOR.GREEN, COLOR.GREEN).get(0).value();
        System.out.println("green and green is " + greenAndGreen.toString());

        COLOR orangeAndBrown = (COLOR) colorHandler.solve(COLOR.ORANGE, COLOR.BROWN).get(0).value();
        System.out.println("orange and brown is " + orangeAndBrown.toString());
    }

    static Rule[] getRules(){
        List<Rule> rules = new ArrayList<>();
        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.RED) && x[1].value().equals(COLOR.BLUE))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.PURPLE))));

        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.BLUE) && x[1].value().equals(COLOR.YELLOW))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.GREEN))));

        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.YELLOW) && x[1].value().equals(COLOR.RED))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.ORANGE))));


        // Version 1: Just Simplification
        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.BROWN) && x[1].value().equals(COLOR.BLUE))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.BROWN))));
        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.BROWN) && x[1].value().equals(COLOR.RED))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.BROWN))));
        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.BROWN) && x[1].value().equals(COLOR.YELLOW))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.BROWN))));
        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.BROWN) && x[1].value().equals(COLOR.PURPLE))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.BROWN))));
        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.BROWN) && x[1].value().equals(COLOR.GREEN))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.BROWN))));
        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.BROWN) && x[1].value().equals(COLOR.ORANGE))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.BROWN))));
        rules.add(new Simplification(2)
                .guard(x -> x[0].value().equals(COLOR.BROWN) && x[1].value().equals(COLOR.BROWN))
                .body((oldC, newC) -> newC.add(new Constraint<>(COLOR.BROWN))));


//        // Version 2: Simpagation instead of Simplification:
//        rules.add(new Simpagation(1, 1).guard((head1, head2) -> head1[0].value().equals(color.brown))
//        // .body((head1, head2, newC) -> {  })   // Body is not necessary
//        );

        // Additional:
        // remove duplicates:
        rules.add(new Simpagation(1, 1).guard((head1, head2) -> head1[0].value().equals(head2[0])));

        return rules.toArray(new Rule[0]);
    }
}
