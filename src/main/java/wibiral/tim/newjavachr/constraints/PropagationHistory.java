package wibiral.tim.newjavachr.constraints;

import wibiral.tim.newjavachr.rules.Rule;

public class PropagationHistory {
    private static class HistoryElement {
        final long ruleID;
        final long[] constraintIDs;

        HistoryElement(Rule rule, Constraint<?>[] constraints){
            ruleID = rule.ID();
        }
    }
}
