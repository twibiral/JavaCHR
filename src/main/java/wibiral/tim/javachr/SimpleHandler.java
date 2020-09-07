package wibiral.tim.javachr;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.tracing.CommandLineTracer;
import wibiral.tim.javachr.tracing.Tracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SimpleHandler extends ConstraintHandler {
    /**
     * Contains for all header the rules have a List with all rules with that specific header size.
     */
    protected final HashMap<Integer, List<Rule>> ruleHash;
    protected final List<Integer> headerSizes = new LinkedList<>();

    private Tracer tracer;
    private boolean tracingOn = false;

    public SimpleHandler(Rule... rules) {
        super(rules);
        ruleHash = instantiateRuleHash();
    }

    public SimpleHandler(Rule rule) {
        super(rule);
        ruleHash = instantiateRuleHash();
    }

    public SimpleHandler() {
        super();
        ruleHash = instantiateRuleHash();
    }

    @Override
    public boolean trace() {
        tracingOn = !tracingOn;
        tracer = tracer == null ? new CommandLineTracer() : tracer;
        return tracingOn;
    }

    private HashMap<Integer, List<Rule>> instantiateRuleHash(){
        final HashMap<Integer, List<Rule>> temp = new HashMap<>(3 * rules.size());

        for (Rule rule : rules) {
            handleRuleAndRuleHash(rule, temp);
        }

        return temp;
    }

    @Override
    public boolean addRule(Rule rule) {
        handleRuleAndRuleHash(rule, ruleHash);

        return rules.add(rule);
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

    @Override
    public ConstraintStore solve(ConstraintStore store) {
        if(tracingOn)
            tracer.startMessage(store);

        boolean ruleApplied = true;
        while (ruleApplied) {
            ruleApplied = false;

            for (int size : headerSizes) {
                if(size <= store.size()){
                    List<Rule> sameHeaderRules = ruleHash.get(size);

                    for (Rule rule : sameHeaderRules) {
                        int[] selectedIdx = findAssignment(store, rule, new int[size], 0);

                        ConstraintStore temp = new ConstraintStore();
                        for (int i : selectedIdx) {
                            temp.add(store.get(i));
                        }

                        if(rule.accepts(temp)){
                            Constraint<?>[] before = tracingOn ? temp.getAll().toArray(new Constraint<?>[0]) : null;

                            store.removeAll(selectedIdx);
                            ruleApplied = rule.apply(temp);
                            store.addAll(temp);

                            Constraint<?>[] after = tracingOn ? temp.getAll().toArray(new Constraint<?>[0]) : null;
                            if(tracingOn && !tracer.step(rule, before, after)){
                                tracer.stopMessage(store);
                                return store;
                            }
                        }
                    }

                }
            }

        }

        if (tracingOn)
            tracer.terminatedMessage(store);

        return store;
    }

    protected int[] findAssignment(ConstraintStore store, Rule rule, int[] selectedIdx, int pos) {
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
