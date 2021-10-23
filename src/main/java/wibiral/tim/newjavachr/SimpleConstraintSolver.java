package wibiral.tim.newjavachr;

import wibiral.tim.newjavachr.rules.Rule;
import wibiral.tim.newjavachr.tracing.Tracer;

import java.util.*;

public class SimpleConstraintSolver implements ConstraintSolver {
    /**
     * Contains for all header the rules have a List with all rules with that specific header size.
     */
    protected final HashMap<Integer, List<Rule>> ruleHash;
    protected final List<Integer> headerSizes = new LinkedList<>();
    protected final List<Rule> rules = new ArrayList<>();

    protected Tracer tracer;
    protected boolean tracingOn = false;
    protected ConstraintStore store;

    public SimpleConstraintSolver(Rule... rules) {
        ruleHash = instantiateRuleHash();
    }

    public SimpleConstraintSolver(Rule rule) {
        ruleHash = instantiateRuleHash();
    }

    public SimpleConstraintSolver() {
        ruleHash = instantiateRuleHash();
    }

    @Override
    public void setTracer(Tracer tracer) {
        tracingOn = tracer != null;
        this.tracer = tracer;
    }

    @Override
    public void addRule(Rule rule) {
        handleRuleAndRuleHash(rule, ruleHash);
        rules.add(rule);
    }

    @Override
    public List<Constraint<?>> solve(List<Constraint<?>> constraints) {
        store = new ConstraintStore(constraints);

        if(tracingOn)
            tracer.initMessage(store);
// TODO: Reimplement algorithm!
//        boolean ruleApplied = true;
//        while (ruleApplied) {
//            ruleApplied = false;
//
//            for (int size : headerSizes) {
//                if(size <= store.size()){
//                    List<Rule> sameHeaderRules = ruleHash.get(size);
//
//                    for (Rule rule : sameHeaderRules) {
//                        int[] selectedIdx = findAssignment(store, rule, new int[size], 0);
//
//                        ConstraintStore temp = new ConstraintStore();
//                        for (int i : selectedIdx) {
//                            temp.add(store.get(i));
//                        }
//
//                        if(rule.accepts(temp)){
//                            Constraint<?>[] before = tracingOn ? temp.getAll().toArray(new Constraint<?>[0]) : null;
//
//                            store.removeAll(selectedIdx);
//                            ruleApplied = rule.apply(temp);
//                            store.addAll(temp);
//
//                            Constraint<?>[] after = tracingOn ? temp.getAll().toArray(new Constraint<?>[0]) : null;
//                            if(tracingOn && !tracer.step(rule, before, after)){
//                                tracer.stopMessage(store);
//                                return store;
//                            }
//                        }
//                    }
//
//                }
//            }
//
//        }

        if (tracingOn)
            tracer.terminatedMessage(store);

        return store.toList();
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

    private HashMap<Integer, List<Rule>> instantiateRuleHash(){
        final HashMap<Integer, List<Rule>> temp = new HashMap<>(3 * rules.size());

        for (Rule rule : rules) {
            handleRuleAndRuleHash(rule, temp);
        }

        return temp;
    }

    // TODO: Should be renamed
    private void handleRuleAndRuleHash(Rule rule, HashMap<Integer, List<Rule>> ruleHash) {
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

    protected int[] findAssignment(List<Constraint<?>> store, Rule rule, int[] selectedIdx, int pos) {
        if (pos == selectedIdx.length) {
            return selectedIdx;

        } else {
            for (int i = 0; i < store.size(); i++) {
                selectedIdx[pos] = i;
                findAssignment(store, rule, selectedIdx, pos + 1);

                if(notAllEqual(selectedIdx)){
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
     * @param array The array to check.
     * @return Return true if there are duplicates in the entry.
     */
    protected boolean notAllEqual(int[] array){
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

}
