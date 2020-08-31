package wibiral.tim.javachr.tracing;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineTracer extends Tracer {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public boolean step(Rule appliedRule, Constraint<?>[] oldConstraints, Constraint<?>[] addedConstraints) {
        String name = appliedRule.name() == null ? appliedRule.getClass().getSimpleName() : appliedRule.name();
        String output = "Apply rule " + name + "\n" +
                        "to: " + constraintsToString(oldConstraints) + "\n" +
                        "generated: " + constraintsToString(addedConstraints);
        System.out.println(output);

        String input = readLine();
        switch (input.toLowerCase()){
            case "":
                return true;
            case "stop":
            case "!":
                return false;
            default:
                System.out.println("Wrong input!");
        }

        return true;
    }

    private String readLine(){
        String str;
        try {
            str = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            str = "stop";
        }

        return str;
    }

    private String constraintsToString(Constraint<?>[] constraints){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < constraints.length - 1; i++) {
            str.append(constraints[i]).append(", ");
        }

        str.append(constraints[constraints.length - 1]);

        return str.toString();
    }
}
