package wibiral.tim.newjavachr.constraints;

import wibiral.tim.newjavachr.rules.Rule;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The propagation is used to store which constraints were applied to which constraints. This is used to prevent
 * propagation rules from firing multiple times for the same constraints.
 * (Propagation doesn't remove any constraints from the constraint store and could therefore fire possibly fire multiple
 * time for the same constraints if no history is saved.)
 */
public class PropagationHistory {
    // A history list for every rule.
    HashMap<Long, ArrayList<HistoryEntry>> history = new HashMap<>();

    /**
     * Adds a new History Entry to the propagation history with the rule and the constraints that were applied to the rule.
     */
    public void addEntry(Rule rule, Constraint<?>[] constraints){
        if(!history.containsKey(rule.ID())){
            history.put(rule.ID(), new ArrayList<>());
        }

        history.get(rule.ID()).add(new HistoryEntry(rule, constraints));
    }

    public boolean isInHistory(Rule rule, Constraint<?>[] constraints){
        long ruleID = rule.ID();
        if(!history.containsKey(ruleID))
            return false;

        ArrayList<HistoryEntry> ruleHistory = history.get(rule.ID());
        for(HistoryEntry entry : ruleHistory){
            if(entry.nrConstraints == constraints.length && entry.contains(constraints))
                return true;
        }

        return false;
    }

    /**
     * This class is used to store a single entry in the Propagation History and contains the ID of a rule and the
     * IDs of the constraints that were applied to the rule.
     */
    private static class HistoryEntry {
        final long ruleID;
        final long[] constraintIDs;
        final int nrConstraints;

        HistoryEntry(Rule rule, Constraint<?>[] constraints){
            ruleID = rule.ID();
            nrConstraints = constraints.length;
            constraintIDs = new long[nrConstraints];

            for (int i = 0; i < nrConstraints; i++) {
                constraintIDs[i] = constraints[i].ID();
            }
        }

        boolean contains(Constraint<?>[] constraints){
            for (int i = 0; i < constraints.length; i++) {
                if (constraints[i].ID() != constraintIDs[i])
                    return false;
            }

            return true;
        }
    }
}
