package wibiral.tim.javachr.tracing;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineTracer extends Tracer {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";

    @Override
    public boolean step(Rule appliedRule, Constraint<?>[] oldConstraints, Constraint<?>[] addedConstraints) {
        String name = appliedRule.name() == null ? appliedRule.getClass().getSimpleName() : appliedRule.name();
        String output = "Apply rule " + name + "\n" +
                        "to: " + constraintsToString(oldConstraints) + "\n" +
                        "generated: " + constraintsToString(addedConstraints);
        print(output);

        String input = readLine();
        switch (input.toLowerCase()){
            case "":
            case ".":
                return true;

            case "stop":
            case "!":
                return false;

            default:
                print("Wrong input!");
        }

        return true;
    }

    @Override
    public void startMessage(ConstraintStore store) {
        print("\n=== Executing handler ===");
        print("Constraints: " + constraintsToString(store.getAll().toArray(new Constraint<?>[0])) + "\n");
    }

    @Override
    public void stopMessage(ConstraintStore store) {
        print("=== Stopping execution... ===\n");
    }

    @Override
    public void terminatedMessage(ConstraintStore store) {
        print("Constraints after execution: " + constraintsToString(store.getAll().toArray(new Constraint<?>[0])));
        print("=== Terminating handler ===\n");
    }

    private void print(String toPrint){
        System.out.println(ANSI_BLUE + toPrint + ANSI_RESET);
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
            str.append(constraints[i].type().getSimpleName()).append(": ")
                    .append(constraints[i].value()).append(", ");
        }

        str.append(constraints[constraints.length-1].type().getSimpleName()).append(": ")
                .append(constraints[constraints.length-1].value());

        return str.toString();
    }
}
