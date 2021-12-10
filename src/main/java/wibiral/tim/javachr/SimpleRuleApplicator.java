package wibiral.tim.javachr;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.constraints.PropagationHistory;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.head.HEAD_DEFINITION_TYPE;
import wibiral.tim.javachr.rules.head.Head;
import wibiral.tim.javachr.rules.head.VAR;
import wibiral.tim.javachr.tracing.Tracer;

import java.util.*;
import java.util.logging.Logger;

/**
 * TODO: Complete refactoring!
 * This rule applicator implements constraint-first matching.
 * This means it takes a combination of constraints and tries to match it with the heads of the rules. The first
 * matching rule is executed. If the constraints fit no rule, the next combination is tried.
 *
 * A matching as described in Thom Frühwirth, "Constraint Handling Rules" (2009) is not possible, because the constraints
 * can't be matched separately but must be matched together.
 */
public class SimpleRuleApplicator implements RuleApplicator {
    protected final List<Rule> rules = new ArrayList<>();

    protected Tracer tracer;
    protected boolean tracingOn = false;

    private int biggestHeader = 0;

    public SimpleRuleApplicator(Rule... rules) {
        this.rules.addAll(Arrays.asList(rules));
        if(rules.length > 2){
            // Find rule with the largest header and set the variable to it.
            // Must be present because the array contains at least 2 rules.
            biggestHeader = this.rules.stream().max(Comparator.comparingInt(Rule::headSize)).get().headSize();

        } else if(rules.length == 1) {
            biggestHeader = rules[0].headSize();

        } else if (rules.length < 1) {
            Logger log = Logger.getLogger(this.getClass().getName());
            log.warning("No rules given to rule applicator.");
        }
    }

    @Override
    public void setTracer(Tracer tracer) {
        tracingOn = tracer != null;
        this.tracer = tracer;
    }

    @Override
    public List<Constraint<?>> execute(List<Constraint<?>> constraints) {
        return execute(new ConstraintStore(constraints));
    }

    @Override
    public List<Constraint<?>> execute(Constraint<?>... constraints) {
        return execute(new ConstraintStore(Arrays.asList(constraints)));
    }

    @SafeVarargs
    @Override
    public final <T> List<Constraint<?>> execute(T... values) {
        return execute(new ConstraintStore(values));
    }

    public List<Constraint<?>> execute(ConstraintStore store) {
        // Stores which rules were already applied to which constraints.
        PropagationHistory history = new PropagationHistory();
        // Is used to build constraint arrays to match them to rule heads.
        ArrayDeque<Iterator<Constraint<?>>> iteratorStack = new ArrayDeque<>(biggestHeader);

        if(tracingOn)
            tracer.initMessage(store);

        boolean ruleApplied = true;
        while(ruleApplied){

            RuleAndMatch ruleAndMatch = findMatch(store, history, iteratorStack);

            if(ruleAndMatch == null){
                ruleApplied = false;

            } else {    // Apply rule.
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

    /**
     * Goes through the rules and tries to find matching constraints for one of the rules.
     * @param store The constraint store that stores the current constraints.
     * @param history The current propagation history.
     * @param iteratorStack The stack of iterators that are used to build the constraint arrays.
     * @return The rule and the matching constraints, or null if no match was found.
     */
    protected RuleAndMatch findMatch(ConstraintStore store,
                                     PropagationHistory history,
                                     ArrayDeque<Iterator<Constraint<?>>> iteratorStack){
        Constraint<?>[] matchingConstraints = null;

        for(Rule rule: rules){
            // Handle every rule according to their header:

            matchingConstraints = findMatchingConstraints(rule, store, iteratorStack, history);

            if(matchingConstraints != null) // match found
                return new RuleAndMatch(rule, matchingConstraints);

        }

        // No match found:
        return null;
    }

    protected Constraint<?>[] findMatchingConstraints(Rule rule,
                                                      ConstraintStore store,
                                                      ArrayDeque<Iterator<Constraint<?>>> iteratorStack,
                                                      PropagationHistory history){
        int headSize = rule.headSize();

        if (headSize > store.size()) {
            // Not enough constraints in the store to apply the rule.
            return null;
        }

        int pointer = 0;    // index of the constraint in the header that is currently matched
        HEAD_DEFINITION_TYPE headDefinitionType = rule.getHeadDefinitionType();
        Iterator<Constraint<?>> currentIter;

        Constraint<?>[] matchingConstraints = new Constraint<?>[headSize];

        switch (headDefinitionType){
            case SIZE_SPECIFIED:
                currentIter = getDefaultIterator(store, rule, 0);
                break;

            case TYPES_SPECIFIED:
                currentIter = getTypeIterator(store, rule, 0);
                break;

            case COMPLEX_DEFINITION:
                currentIter = getComplexHeadIterator(store, rule, 0);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + headDefinitionType);
        }


        boolean allCombinationsTested = false;
        while(!allCombinationsTested){

            if(pointer < headSize-1 && currentIter.hasNext()){
                matchingConstraints[pointer] = currentIter.next();
                iteratorStack.add(currentIter);
                pointer++;

                switch (headDefinitionType){
                    case SIZE_SPECIFIED:
                        currentIter = getDefaultIterator(store, rule, pointer);
                        break;

                    case TYPES_SPECIFIED:
                        currentIter = getTypeIterator(store, rule, pointer);
                        break;

                    case COMPLEX_DEFINITION:
                        currentIter = getComplexHeadIterator(store, rule, pointer);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + headDefinitionType);
                }

            } else if(currentIter.hasNext() && headDefinitionType == HEAD_DEFINITION_TYPE.COMPLEX_DEFINITION){
            // Filling last element in array and try to match
            matchingConstraints[pointer] = currentIter.next();

            if(rule.saveHistory()){ // Rules that want to be saved in the propagation history -> Propagation
                // if all constraints different AND rule+constraints not in history AND fits header+guard
                if (noDuplicatesIn(matchingConstraints)
                        && checkBindings(rule.getVariableBindings(), matchingConstraints)
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
                        && checkBindings(rule.getVariableBindings(), matchingConstraints)
                        && rule.accepts(Arrays.asList(matchingConstraints))) {

                    for(Constraint<?> constraint : matchingConstraints){
                        store.remove(constraint.ID());
                    }

                    return matchingConstraints;
                }
            }

            }else if(currentIter.hasNext()) {
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

        // No matching constraints found
        return null;
    }

    private Iterator<Constraint<?>> getDefaultIterator(ConstraintStore store, Rule rule, int pos) {
        return store.lookup();
    }

    private Iterator<Constraint<?>> getTypeIterator(ConstraintStore store, Rule rule, int pos) {
        if (rule.getHeadDefinitionType() != HEAD_DEFINITION_TYPE.TYPES_SPECIFIED) {
            return getDefaultIterator(store, rule, pos);
        }

        return store.lookup(rule.getHeadTypes()[pos]);
    }

    private Iterator<Constraint<?>> getComplexHeadIterator(ConstraintStore store, Rule rule, int pos) {
        if(rule.getHeadDefinitionType() != HEAD_DEFINITION_TYPE.COMPLEX_DEFINITION){
            return getDefaultIterator(store, rule, pos);
        }

        Head headDefinition = rule.getHeadDefinitions()[pos];

        switch (headDefinition.getHeadConstraintDefType()) {
            case ANY:
                return getDefaultIterator(store, rule, pos);

            case TYPE:
                return store.lookup(headDefinition.getType());

            case VALUE:
                return store.lookup(headDefinition.getValue());

            default:
                throw new IllegalStateException("Unexpected value: " + headDefinition.getHeadConstraintDefType());
        }
    }

    protected Constraint<?>[] matchTypesSpecified(Rule rule,
                                                  ConstraintStore store,
                                                  ArrayDeque<Iterator<Constraint<?>>> iteratorStack,
                                                  PropagationHistory history){
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

    protected Constraint<?>[] matchComplexHeadDefinition(Rule rule,
                                                         ConstraintStore store,
                                                         ArrayDeque<Iterator<Constraint<?>>> iteratorStack,
                                                         PropagationHistory history){
        int pointer = 0;    // index of the constraint in the header that is currently matched

        Head[] headDef = rule.getHeadDefinitions();

        int headerSize = rule.headSize();
        Constraint<?>[] matchingConstraints = new Constraint<?>[headerSize];
        Iterator<Constraint<?>> currentIter = null;
        // Decide which lookup to use, depending on how the header constraint is defined in the rule.
        switch(headDef[pointer].getHeadConstraintDefType()){
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
                switch(headDef[pointer].getHeadConstraintDefType()){
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
                            && checkBindings(rule.getVariableBindings(), matchingConstraints)
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
                            && checkBindings(rule.getVariableBindings(), matchingConstraints)
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
     * @param bindings Contains which head constrains must be equal.
     * @param array Array with potential head constraints.
     * @return True, if all constraints that must be equal are equal. Otherwise, false.
     */
    protected boolean checkBindings(EnumMap<VAR, ArrayList<Integer>> bindings, Constraint<?>[] array){
        for (ArrayList<Integer> bound : bindings.values()){
            for (int i = 0; i < bound.size()-1; i++) {
                // Check if the two constraints are equal
                if(! array[bound.get(i)].get().equals(array[bound.get(i+1)].get()))
                    return false;
            }
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
