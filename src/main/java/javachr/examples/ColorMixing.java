package javachr.examples;

import javachr.RuleApplicator;
import javachr.SimpleRuleApplicator;
import javachr.rules.Rule;
import javachr.rules.Simpagation;
import javachr.rules.Simplification;
import javachr.rules.head.Head;

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
        RuleApplicator colorHandler = new SimpleRuleApplicator(getRules2());
//        colorHandler.setTracer(new CommandLineTracer()); // Use to see the process of the rule application

        COLOR redAndBlue = (COLOR) colorHandler.execute(COLOR.RED, COLOR.BLUE).get(0).get();
        System.out.println("red and blue is " + redAndBlue.toString());

        COLOR redAndYellow = (COLOR) colorHandler.execute(COLOR.RED, COLOR.YELLOW).get(0).get();
        System.out.println("red and yellow is " + redAndYellow.toString());

        COLOR greenAndGreen = (COLOR) colorHandler.execute(COLOR.GREEN, COLOR.GREEN).get(0).get();
        System.out.println("green and green is " + greenAndGreen.toString());

        COLOR orangeAndBrown = (COLOR) colorHandler.execute(COLOR.ORANGE, COLOR.BROWN).get(0).get();
        System.out.println("orange and brown is " + orangeAndBrown.toString());
    }

    public static Rule[] getRules(){
        List<Rule> rules = new ArrayList<>();
        rules.add(new Simplification(Head.ofValue(COLOR.RED), Head.ofValue(COLOR.BLUE))
                .body((head, newConstraints) -> newConstraints.add(COLOR.PURPLE)));

        rules.add(new Simplification(Head.ofValue(COLOR.BLUE), Head.ofValue(COLOR.YELLOW))
                .body((head, newConstraints) -> newConstraints.add(COLOR.GREEN)));

        rules.add(new Simplification(Head.ofValue(COLOR.YELLOW), Head.ofValue(COLOR.RED))
                .body((head, newConstraints) -> newConstraints.add(COLOR.ORANGE)));

        // Version 1: Simplification; every color defined
//        rules.add(new Simplification(Head.OF_VALUE(COLOR.BROWN), Head.OF_VALUE(COLOR.BLUE))
//                .body((head, newConstraints) -> newConstraints.add(COLOR.BROWN)));
//        rules.add(new Simplification(Head.OF_VALUE(COLOR.BROWN), Head.OF_VALUE(COLOR.RED))
//                .body((head, newConstraints) -> newConstraints.add(COLOR.BROWN)));
//        rules.add(new Simplification(Head.OF_VALUE(COLOR.BROWN), Head.OF_VALUE(COLOR.YELLOW))
//                .body((head, newConstraints) -> newConstraints.add(COLOR.BROWN)));
//        rules.add(new Simplification(Head.OF_VALUE(COLOR.BROWN), Head.OF_VALUE(COLOR.PURPLE))
//                .body((head, newConstraints) -> newConstraints.add(COLOR.BROWN)));
//        rules.add(new Simplification(Head.OF_VALUE(COLOR.BROWN), Head.OF_VALUE(COLOR.GREEN))
//                .body((head, newConstraints) -> newConstraints.add(COLOR.BROWN)));
//        rules.add(new Simplification(Head.OF_VALUE(COLOR.BROWN), Head.OF_VALUE(COLOR.ORANGE))
//                .body((head, newConstraints) -> newConstraints.add(COLOR.BROWN)));
//        rules.add(new Simplification(Head.OF_VALUE(COLOR.BROWN), Head.OF_VALUE(COLOR.BROWN))
//                .body((head, newConstraints) -> newConstraints.add(COLOR.BROWN)));

        // Version 2: Simplification; use of wildcard
//        rules.add(new Simplification(Head.OF_VALUE(COLOR.BROWN), Head.ANY())
//                .body((head, newConstraints) -> newConstraints.add(COLOR.BROWN)));

        // Version 3: Simpagation instead of Simplification:
        rules.add(new Simpagation(1, Head.ofValue(COLOR.BROWN), Head.any())
        // .guard((head1, head2) -> head1[0].value().equals(COLOR.BROWN))  // Not necessary
        // .body((head1, head2, newConstraints) -> {  })   // Body is not necessary
        );

        // remove duplicates:
        rules.add(new Simpagation("Remove duplicate", 1, Head.ofType(COLOR.class), Head.ofType(COLOR.class))
                .guard((head1, head2) -> head1[0].get().equals(head2[0])));

        return rules.toArray(new Rule[0]);
    }

    public static Rule[] getRules2(){
        List<Rule> rules = new ArrayList<>();
        rules.add(new Simplification(COLOR.class, COLOR.class)
                .guard(head -> head[0].get().equals(COLOR.RED) && head[1].get().equals(COLOR.BLUE))
                .body((head, newConstraints) -> newConstraints.add(COLOR.PURPLE)));

        rules.add(new Simplification(COLOR.class, COLOR.class)
                .guard(head -> head[0].get().equals(COLOR.BLUE) && head[1].get().equals(COLOR.YELLOW))
                .body((head, newConstraints) -> newConstraints.add(COLOR.GREEN)));

        rules.add(new Simplification(COLOR.class, COLOR.class)
                .guard(head -> head[0].get().equals(COLOR.YELLOW) && head[1].get().equals(COLOR.RED))
                .body((head, newConstraints) -> newConstraints.add(COLOR.ORANGE)));

        // Mixing with brown results in brown:
        rules.add(new Simpagation(1, COLOR.class, COLOR.class)
                .guard((head1, head2) -> head1[0].get().equals(COLOR.BROWN))
                // .body((head1, head2, newConstraints) -> {  })   // Body is not necessary
        );

        // remove duplicates:
        rules.add(new Simpagation("Remove duplicate", 1, COLOR.class, COLOR.class)
                .guard((head1, head2) -> head1[0].get().equals(head2[0])));

        return rules.toArray(new Rule[0]);
    }

    public static Rule[] getRules3(){
        List<Rule> rules = new ArrayList<>();
        rules.add(new Simplification(2)
                .guard(head -> head[0].get().equals(COLOR.RED) && head[1].get().equals(COLOR.BLUE))
                .body((head, newConstraints) -> newConstraints.add(COLOR.PURPLE)));

        rules.add(new Simplification(2)
                .guard(head -> head[0].get().equals(COLOR.BLUE) && head[1].get().equals(COLOR.YELLOW))
                .body((head, newConstraints) -> newConstraints.add(COLOR.GREEN)));

        rules.add(new Simplification(2)
                .guard(head -> head[0].get().equals(COLOR.YELLOW) && head[1].get().equals(COLOR.RED))
                .body((head, newConstraints) -> newConstraints.add(COLOR.ORANGE)));

        // Mixing with brown results in brown:
        rules.add(new Simpagation(1, 1).guard((head1, head2) -> head1[0].get().equals(COLOR.BROWN))
                // .body((head1, head2, newConstraints) -> {  })   // Body is not necessary
        );

        // remove duplicates:
        rules.add(new Simpagation("Remove duplicate", 1, 1)
                .guard((head1, head2) -> head1[0].get().equals(head2[0])));

        return rules.toArray(new Rule[0]);
    }

}
