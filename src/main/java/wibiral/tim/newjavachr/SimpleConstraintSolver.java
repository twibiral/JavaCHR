package wibiral.tim.newjavachr;

import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.constraints.ConstraintStore;
import wibiral.tim.newjavachr.constraints.PropagationHistory;
import wibiral.tim.newjavachr.rules.Rule;
import wibiral.tim.newjavachr.tracing.Tracer;

import java.util.*;

/**
 * This constraint solver implements constraint-first matching.
 * This means it takes a combination of constraints and tries to match it with the heads of the rules. The first
 * matching rule is executed. If the constraints fit no rule, the next combination is tried.
 *
 * A matching as described in Thom Frühwirth, "Constraint Handling Rules" (2009) is not possible, because the constraints
 * can't be matched separately but must be matched together.
 */
public class SimpleConstraintSolver implements ConstraintSolver {
    /**
     * Contains for all header the rules have a List with all rules with that specific header size.
     * (The header sizes are the key)
     */
    protected final HashMap<Integer, List<Rule>> ruleHash;
    protected final List<Integer> headerSizes = new LinkedList<>();
    protected final List<Rule> rules = new ArrayList<>();

    /**
     * Constains a history that stores which rules were applied to which constraints.
     */
    protected PropagationHistory history;
    protected Tracer tracer;
    protected boolean tracingOn = false;
    protected ConstraintStore store;


    public SimpleConstraintSolver(Rule... rules) {
        this.rules.addAll(Arrays.asList(rules));
        ruleHash = instantiateRuleHash(this.rules);
    }

    public SimpleConstraintSolver(Rule rule) {
        this.rules.add(rule);
        ruleHash = instantiateRuleHash(this.rules);
    }

    public SimpleConstraintSolver() {
        ruleHash = instantiateRuleHash(rules);
    }

    @Override
    public void setTracer(Tracer tracer) {
        tracingOn = tracer != null;
        this.tracer = tracer;
    }

    @Override
    public void addRule(Rule rule) {
        hashRule(rule, ruleHash);
        rules.add(rule);
    }

    @Override
    public List<Constraint<?>> solve(List<Constraint<?>> constraints) {
        store = new ConstraintStore(constraints);
        history = new PropagationHistory();

        if(tracingOn)
            tracer.initMessage(store);

        boolean ruleApplied = true;
        while(ruleApplied){

            RuleAndMatch ruleAndMatch = findMatch();
            if(ruleAndMatch == null){
                ruleApplied = false;

            } else {
                List<Constraint<?>> constraintList = new ArrayList<>(Arrays.asList(ruleAndMatch.match));
                if(ruleAndMatch.rule.saveHistory()){
                    history.addEntry(ruleAndMatch.rule, ruleAndMatch.match);
                }

                // Store directly in the old variable to save memory and time.
                constraintList = ruleAndMatch.rule.apply(constraintList);
                store.addAll(constraintList);

                if(tracingOn)
                    tracer.step(ruleAndMatch.rule, ruleAndMatch.match, constraintList.toArray(new Constraint<?>[0]));
            }

        }

        if(tracingOn)
            tracer.terminatedMessage(store);

        List<Constraint<?>> result = store.toList();
        store = null;

        return result;
    }

    @Override
    public List<Constraint<?>> solve(Constraint<?>... constraints) {
        return solve(Collections.addAll(new ArrayList<Constraint<?>>(), constraints));
    }

    @Override
    public <T> List<Constraint<?>> solve(T... values) {
        ArrayList<Constraint<?>> constraints = new ArrayList<>();
        for(T val : values)
            constraints.add(new Constraint<>(val));
        return solve(constraints);
    }

    private HashMap<Integer, List<Rule>> instantiateRuleHash(List<Rule> rules){
        final HashMap<Integer, List<Rule>> temp = new HashMap<>(3 * rules.size());

        for (Rule rule : rules) {
            hashRule(rule, temp);
        }

        return temp;
    }

    /**
     * @return Array with constraints that match header
     */
    protected RuleAndMatch findMatch(){
        headerSizes.sort(Integer::compare); // sort list that contains all different header sizes.
        int biggestHeader = headerSizes.get(headerSizes.size() - 1);

        ArrayDeque<Iterator<Constraint<?>>> iteratorStack = new ArrayDeque<>(biggestHeader);
        int pointer = 0;    // index of the constraint in the header that is currently matched

        for(int hSize : headerSizes){
            Constraint<?>[] matchingConstraints = new Constraint<?>[hSize];
            Iterator<Constraint<?>> currentIter = store.lookup();

            boolean allCombinationsTested = false;
            while(!allCombinationsTested){

                if(pointer < hSize-1 && currentIter.hasNext()){
                    matchingConstraints[pointer] = currentIter.next();
                    iteratorStack.add(currentIter);
                    currentIter = store.lookup();
                    pointer++;

                } else if(currentIter.hasNext()) {
                    matchingConstraints[pointer] = currentIter.next();

                    for (Rule rule : ruleHash.get(hSize)) {

                        // if all constraints different AND rule+constraints not in history AND fits header+guard
                        if (noDuplicatesIn(matchingConstraints)
                                && !history.isInHistory(rule, matchingConstraints)
                                && rule.accepts(Arrays.asList(matchingConstraints))) {

                            for(Constraint<?> constraint : matchingConstraints){
                                store.remove(constraint.ID());
                            }

                            return new RuleAndMatch(rule, matchingConstraints);
                        }
                    }

                } else if(pointer > 0){
                    pointer--;
                    currentIter = iteratorStack.removeLast();

                } else {
                    allCombinationsTested = true;
                }

            }

        }

        // No match found:
        return null;
    }

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
     * Add rule to the hash.
     */
    private void hashRule(Rule rule, HashMap<Integer, List<Rule>> ruleHash) {
        int headerSize = rule.headSize();
        if (ruleHash.containsKey(headerSize)) {
            ruleHash.get(headerSize).add(rule);

        } else {
            List<Rule> ruleList = new ArrayList<>();
            ruleList.add(rule);
            ruleHash.put(headerSize, ruleList);
            headerSizes.add(headerSize);
        }
    }

    /**
     * Stores a rule and a fitting match.
     */
    private static class RuleAndMatch {
        final Rule rule;
        final Constraint<?>[] match;

        RuleAndMatch(Rule rule, Constraint<?>[] match){
            this.rule = rule;
            this.match = match;
        }
    }
}
