package javachr.extensions.higherorderrules;

import javachr.constraints.Constraint;
import javachr.rules.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Twice extends HigherOrderBase {
    protected Twice(Rule rule) {
        super(rule, "Twice");
    }

    @Override
    public boolean accepts(List<Constraint<?>> constraints) {
        return this.rule.accepts(constraints);
    }

    @Override
    public List<Constraint<?>> apply(List<Constraint<?>> constraints) {
        List<Constraint<?>> result1 = this.rule.apply(new ArrayList<>(constraints));// Works on copy
        List<Constraint<?>> result2 = this.rule.apply(constraints);

        return Stream.concat(result1.stream(), result2.stream())    // join lists
                .distinct()                                         // remove duplicates
                .collect(Collectors.toList());                      // convert to list
    }
}
