package wibiral.tim.newjavachr;

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

    protected Tracer tracer;
    protected boolean tracingOn = false;
    protected ConstraintStore store;

    private boolean sorted = false;

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
        sorted = false;
    }

    @Override
    public List<Constraint<?>> solve(List<Constraint<?>> constraints) {
        store = new ConstraintStore(constraints);
        sortRules();

        if(tracingOn)
            tracer.initMessage(store);

// TODO: Reimplement algorithm!

        boolean ruleApplied = true;
        while(ruleApplied){
            ruleApplied = false;



        }

        return terminate();
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
     * Finds matching constraints for the rule.
     * @return constraint with indices of the matching constraints.
     */
    protected int[] findAssignment(List<Constraint<?>> store, Rule rule, int[] selectedIdx, int pos) {
        if (pos == selectedIdx.length) {
            return selectedIdx;

        } else {
            for (int i = 0; i < store.size(); i++) {
                selectedIdx[pos] = i;
                findAssignment(store, rule, selectedIdx, pos + 1);

                if(noDuplicatesIn(selectedIdx)){
                    List<Constraint<?>> constraints = new ArrayList<>();
                    for (int j : selectedIdx) {
                        constraints.add(store.get(j));
                    }

                    if (rule.accepts(constraints)) {
                        return selectedIdx;
                    }
                }
            }
        }

        return selectedIdx;
    }

    /**
     * Executes cleanup and calls tracer.
     * @return The content of the constraint store after execution.
     */
    protected List<Constraint<?>> terminate(){
        if(tracingOn)
            tracer.terminatedMessage(store);
        List<Constraint<?>> result = store.toList();
        store = null;
        return result;
    }

    /**
     * @param array The array to check.
     * @return Return true if there are duplicates in the entry.
     */
    protected boolean noDuplicatesIn(int[] array){
        for(int i : array){
            int cnt = 0;
            for(int j : array){
                cnt += j == i ? 1 : 0;
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
     * Sorts the list with rules by head size.
     */
    private void sortRules(){
        if(sorted)
            return;

        Collections.sort(rules, new Comparator<Rule>() {
            @Override
            public int compare(Rule lhs, Rule rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return Integer.compare(lhs.headSize(), rhs.headSize());
            }
        });

        sorted = true;
    }

}
