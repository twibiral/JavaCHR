package wibiral.tim.newjavachr.tracing;

import wibiral.tim.newjavachr.Constraint;
import wibiral.tim.newjavachr.ConstraintStore;
import wibiral.tim.newjavachr.rules.Rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A tracer that prints simple statements to the command line. Helpful for debugging.
 * TODO: Replace System.out by logger.
 */
public class CommandLineTracer implements Tracer {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";

    private final boolean stopAfterEveryStep;

    public CommandLineTracer(){
        stopAfterEveryStep = false;
    }

    /**
     * @param stopAfterEveryStep If true: Asks user for permission to execute next step for every step. (Press enter to execute next step, type "!" or "stop" to stop execution.)
     */
    public CommandLineTracer(boolean stopAfterEveryStep){
        this.stopAfterEveryStep = stopAfterEveryStep;
    }

    @Override
    public boolean step(Rule appliedRule, Constraint<?>[] oldConstraints, Constraint<?>[] addedConstraints) {
        String name = appliedRule.name() == null ? appliedRule.getClass().getSimpleName() : "'" + appliedRule.name() + "'";
        String output = "Apply rule " + name + "\n" +
                        "on: " + constraintsToString(oldConstraints) + "\n" +
                        "generated: " + constraintsToString(addedConstraints);
        print(output);

        System.out.flush();

        if(stopAfterEveryStep){
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
        }

        return true;
    }

    @Override
    public void initMessage(ConstraintStore store) {
        print("\n=== Executing handler ===");
        print("Constraints: " + constraintsToString(store.toList().toArray(new Constraint<?>[0])) + "\n");
    }

    @Override
    public void stopMessage(ConstraintStore store) {
        print("=== Stopping execution... ===\n");
    }

    @Override
    public void terminatedMessage(ConstraintStore store) {
        print("Constraints after execution: " + constraintsToString(store.toList().toArray(new Constraint<?>[0])));
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
        if(constraints.length == 0)
            return "--";

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
