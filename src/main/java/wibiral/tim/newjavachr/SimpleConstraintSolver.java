package wibiral.tim.newjavachr;

import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.constraints.ConstraintStore;
import wibiral.tim.newjavachr.constraints.PropagationHistory;
import wibiral.tim.newjavachr.rules.Rule;
import wibiral.tim.newjavachr.rules.head.Head;
import wibiral.tim.newjavachr.tracing.Tracer;

import java.util.*;

/**
 * TODO: Complete refactoring!
 * This constraint solver implements constraint-first matching.
 * This means it takes a combination of constraints and tries to match it with the heads of the rules. The first
 * matching rule is executed. If the constraints fit no rule, the next combination is tried.
 *
 * A matching as described in Thom Frühwirth, "Constraint Handling Rules" (2009) is not possible, because the constraints
 * can't be matched separately but must be matched together.
 */
public class SimpleConstraintSolver implements ConstraintSolver {
    protected final List<Rule> rules = new ArrayList<>();

    /**
     * Constains a history that stores which rules were applied to which constraints.
     */
    protected PropagationHistory history = new PropagationHistory();
    protected Tracer tracer;
    protected boolean tracingOn = false;

    private int biggestHeader = 0;


    public SimpleConstraintSolver(Rule... rules) {
        this.rules.addAll(Arrays.asList(rules));
        if(rules.length > 2){
            // Find rule with the largest header and set the variable to it.
            // Must be present because the array contains at least 2 rules.
            biggestHeader = this.rules.stream().max(Comparator.comparingInt(Rule::headSize)).get().headSize();

        } else if(rules.length == 1) {
            biggestHeader = rules[0].headSize();

        } else if (rules.length < 1) {
            System.err.println("No rules given!");
        }
    }

    @Override
    public void setTracer(Tracer tracer) {
        tracingOn = tracer != null;
        this.tracer = tracer;
    }

    @Override
    public void addRule(Rule rule) {
        rules.add(rule);
    }

    @Override
    public List<Constraint<?>> solve(List<Constraint<?>> constraints) {
        return solve(new ConstraintStore(constraints));
    }

    @Override
    public List<Constraint<?>> solve(Constraint<?>... constraints) {
        return solve(new ConstraintStore(Arrays.asList(constraints)));
    }

    @SafeVarargs
    @Override
    public final <T> List<Constraint<?>> solve(T... values) {
        return solve(new ConstraintStore(values));
    }

    // ===================== Experimental ================================
    public List<Constraint<?>> solve(ConstraintStore store) {
        // TODO: make store and history local variables for better concurrency safety
        history = new PropagationHistory();

        if(tracingOn)
            tracer.initMessage(store);

        boolean ruleApplied = true;
        while(ruleApplied){

            RuleAndMatch ruleAndMatch = findMatch(store);

            if(ruleAndMatch == null){
                ruleApplied = false;

            } else {
                List<Constraint<?>> constraintList = new ArrayList<>(Arrays.asList(ruleAndMatch.match));
                if(tracingOn){
                    List<Constraint<?>> temp = new ArrayList<>(constraintList);
                    temp.removeAll(new ArrayList<>(Arrays.asList(ruleAndMatch.match)));
                    tracer.step(ruleAndMatch.rule, ruleAndMatch.match, temp.toArray(new Constraint<?>[0]));
                }

                if(ruleAndMatch.rule.saveHistory()){
                    history.addEntry(ruleAndMatch.rule, ruleAndMatch.match);
                }

                // Store directly in the old variable to save memory and time.
                constraintList = ruleAndMatch.rule.apply(constraintList);
                store.addAll(constraintList);

            }

        }

        if(tracingOn)
            tracer.terminatedMessage(store);

        return store.toList();
    }

    protected RuleAndMatch findMatch(ConstraintStore store){
        Constraint<?>[] matchingConstraints = null;
        ArrayDeque<Iterator<Constraint<?>>> iteratorStack = new ArrayDeque<>(biggestHeader);

        for(Rule rule: rules){
            // Handle every rule according to their header:
            switch(rule.getHeadDefinitionType()){
                case SIZE_SPECIFIED:
                    matchingConstraints = matchSizeSpecified(rule, store, iteratorStack);
                    break;

                case TYPES_SPECIFIED:
                    matchingConstraints = matchTypesSpecified(rule, store, iteratorStack);
                    break;

                case COMPLEX_DEFINITION:
                    matchingConstraints = matchComplexHeadDefinition(rule, store, iteratorStack);
                    break;
            }

            if(matchingConstraints != null) // match found
                return new RuleAndMatch(rule, matchingConstraints);

        }

        // No match found:
        return null;
    }

    protected Constraint<?>[] matchSizeSpecified(Rule rule, ConstraintStore store, ArrayDeque<Iterator<Constraint<?>>> iteratorStack){
        int pointer = 0;    // index of the constraint in the header that is currently matched

        int headerSize = rule.headSize();
        Constraint<?>[] matchingConstraints = new Constraint<?>[headerSize];
        Iterator<Constraint<?>> currentIter = store.lookup();

        boolean allCombinationsTested = false;
        while(!allCombinationsTested){

            if(pointer < headerSize-1 && currentIter.hasNext()){
                matchingConstraints[pointer] = currentIter.next();
                iteratorStack.add(currentIter);
                currentIter = store.lookup();
                pointer++;

            } else if(currentIter.hasNext()) {
                // Filling last element in array and try to match
                matchingConstraints[pointer] = currentIter.next();

                if(rule.saveHistory()){ // Rules that want to be saved in the propagation history -> Propagation
                    // if all constraints different AND rule+constraints not in history AND fits header+guard
                    if (noDuplicatesIn(matchingConstraints)
                            && !history.isInHistory(rule, matchingConstraints)
                            && rule.accepts(Arrays.asList(matchingConstraints))) {

                        for(Constraint<?> constraint : matchingConstraints){
                            store.remove(constraint.ID());
                        }

                        return matchingConstraints;
                    }

                } else { // Rules that allow to be executed on the same constraints multiple times
                    // if all constraints different AND fits header+guard
                    if (noDuplicatesIn(matchingConstraints)
                            && rule.accepts(Arrays.asList(matchingConstraints))) {

                        for(Constraint<?> constraint : matchingConstraints){
                            store.remove(constraint.ID());
                        }

                        return matchingConstraints;
                    }
                }

            } else if(pointer > 0){
                pointer--;
                currentIter = iteratorStack.removeLast();

            } else {
                allCombinationsTested = true;
            }
        }

        // NO matching constraints found
        return null;
    }

    protected Constraint<?>[] matchTypesSpecified(Rule rule, ConstraintStore store, ArrayDeque<Iterator<Constraint<?>>> iteratorStack){
        int pointer = 0;    // index of the constraint in the header that is currently matched

        Class<?>[] headTypes = rule.getHeadTypes();

        int headerSize = rule.headSize();
        Constraint<?>[] matchingConstraints = new Constraint<?>[headerSize];
        Iterator<Constraint<?>> currentIter = store.lookup(headTypes[0]);

        boolean allCombinationsTested = false;
        while(!allCombinationsTested){

            if(pointer < headerSize-1 && currentIter.hasNext()){
                // Take constraint from iter and add iter to stack
                matchingConstraints[pointer] = currentIter.next();
                iteratorStack.add(currentIter);

                // Find constraint for next position of array
                pointer++;
                currentIter = store.lookup(headTypes[pointer]);

            } else if(currentIter.hasNext()) {
                // Filling last element in array and try to match
                matchingConstraints[pointer] = currentIter.next();

                if(rule.saveHistory()){ // Rules that want to be saved in the propagation history -> Propagation
                    // if all constraints different AND rule+constraints not in history AND fits header+guard
                    if (noDuplicatesIn(matchingConstraints)
                            && !history.isInHistory(rule, matchingConstraints)
                            && rule.accepts(Arrays.asList(matchingConstraints))) {

                        for(Constraint<?> constraint : matchingConstraints){
                            store.remove(constraint.ID());
                        }

                        return matchingConstraints;
                    }

                } else { // Rules that allow to be executed on the same constraints multiple times
                    // if all constraints different AND fits header+guard
                    if (noDuplicatesIn(matchingConstraints)
                            && rule.accepts(Arrays.asList(matchingConstraints))) {

                        for(Constraint<?> constraint : matchingConstraints){
                            store.remove(constraint.ID());
                        }

                        return matchingConstraints;
                    }
                }

            } else if(pointer > 0){
                pointer--;
                currentIter = iteratorStack.removeLast();

            } else {
                allCombinationsTested = true;
            }
        }

        // NO matching constraints found
        return null;
    }

    protected Constraint<?>[] matchComplexHeadDefinition(Rule rule, ConstraintStore store, ArrayDeque<Iterator<Constraint<?>>> iteratorStack){
        int pointer = 0;    // index of the constraint in the header that is currently matched

        Head[] headDef = rule.getHeadDefinitions();

        int headerSize = rule.headSize();
        Constraint<?>[] matchingConstraints = new Constraint<?>[headerSize];
        Iterator<Constraint<?>> currentIter = null;
        // Decide which lookup to use, depending on how the header constraint is defined in the rule.
        switch(headDef[pointer].getContainerType()){
            case ANY:
                currentIter = store.lookup();
                break;

            case TYPE:
                currentIter = store.lookup(headDef[pointer].getType());
                break;

            case VALUE:
                currentIter = store.lookup(headDef[pointer].getValue());
                break;
        }

        boolean allCombinationsTested = false;
        while(!allCombinationsTested){

            if(pointer < headerSize-1 && currentIter.hasNext()){
                matchingConstraints[pointer] = currentIter.next();
                iteratorStack.add(currentIter);
                pointer++;

                // Decide which lookup to use, depending on how the header constraint is defined in the rule.
                switch(headDef[pointer].getContainerType()){
                    case ANY:
                        currentIter = store.lookup();
                        break;

                    case TYPE:
                        currentIter = store.lookup(headDef[pointer].getType());
                        break;

                    case VALUE:
                        currentIter = store.lookup(headDef[pointer].getValue());
                        break;
                }


            } else if(currentIter.hasNext()) {
                // Filling last element in array and try to match
                matchingConstraints[pointer] = currentIter.next();

                if(rule.saveHistory()) { // Rules that want to be saved in the propagation history -> Propagation
                    // if all constraints different AND rule+constraints not in history AND fits header+guard
                    if (noDuplicatesIn(matchingConstraints)
                            && !history.isInHistory(rule, matchingConstraints)
                            && rule.accepts(Arrays.asList(matchingConstraints))) {

                        for (Constraint<?> constraint : matchingConstraints) {
                            store.remove(constraint.ID());
                        }

                        return matchingConstraints;
                    }

                } else { // Rules that allow to be executed on the same constraints multiple times
                    // if all constraints different AND fits header+guard
                    if (noDuplicatesIn(matchingConstraints)
                            && rule.accepts(Arrays.asList(matchingConstraints))) {

                        for(Constraint<?> constraint : matchingConstraints){
                            store.remove(constraint.ID());
                        }

                        return matchingConstraints;
                    }
                }

            } else if(pointer > 0) {
                pointer--;
                currentIter = iteratorStack.removeLast();

            } else {
                allCombinationsTested = true;
            }
        }

        // No matching constraints found
        return null;
    }

    // ===================================================================

    /**
     * @param array The array to check.
     * @return Return true if there are duplicates in the entry.
     */
    protected boolean noDuplicatesIn(Constraint<?>[] array){
        for(Constraint<?> i : array){
            int cnt = 0;
            for(Constraint<?> j : array){
                cnt += j.ID() == i.ID() ? 1 : 0;
            }
            if(cnt > 1)
                return false;
        }
        return true;
    }

    /**
     * Stores a rule and a fitting match.
     */
    protected static class RuleAndMatch {
        final Rule rule;
        final Constraint<?>[] match;

        RuleAndMatch(Rule rule, Constraint<?>[] match){
            this.rule = rule;
            this.match = match;
        }
    }
}
