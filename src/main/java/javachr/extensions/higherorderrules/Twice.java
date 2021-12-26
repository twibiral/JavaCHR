package javachr.extensions.higherorderrules;

import javachr.constraints.Constraint;
import javachr.rules.Rule;
import javachr.rules.head.HEAD_DEFINITION_TYPE;
import javachr.rules.head.Head;
import javachr.rules.head.VAR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Twice extends Rule {
    private final Rule rule;

    protected Twice(Rule rule) {
        super("Twice " + rule.name(), rule.headSize());
        this.rule = rule;
    }

    @Override
    public boolean accepts(Constraint<?>[] constraints) {
        return rule.accepts(constraints);
    }

    @Override
    public List<Constraint<?>> apply(Constraint<?>[] constraints) {
        List<Constraint<?>> result1 = rule.apply(constraints);// Works on copy
        List<Constraint<?>> result2 = rule.apply(constraints);

        return Stream.concat(result1.stream(), result2.stream())    // join lists
                .distinct()                                         // remove duplicates
                .collect(Collectors.toList());                      // convert to list
    }

    @Override
    public HEAD_DEFINITION_TYPE getHeadDefinitionType() {
        return rule.getHeadDefinitionType();
    }

    @Override
    public Class<?>[] getHeadTypes(){
        return rule.getHeadTypes();
    }

    @Override
    public Head[] getHeadDefinitions(){
        return rule.getHeadDefinitions();
    }

    @Override
    public Map<VAR, ArrayList<Integer>> getVariableBindings() {
        return rule.getVariableBindings();
    }

    @Override
    public boolean saveHistory() {
        return rule.saveHistory();
    }
}
