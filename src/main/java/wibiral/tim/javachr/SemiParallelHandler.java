package wibiral.tim.javachr;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.tracing.CommandLineTracer;
import wibiral.tim.javachr.tracing.Tracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SemiParallelHandler extends ConstraintHandler {
    /**
     * Contains for all header the rules have a List with all rules with that specific header size.
     */
    private final HashMap<Integer, List<Rule>> ruleHash;
    private final List<Integer> headerSizes = new ArrayList<>();
    private final ThreadPool pool;

    private Tracer tracer;
    private boolean tracingOn = false;

    public SemiParallelHandler(int workerThreads, Rule... rules) {
        super(rules);
        ruleHash = instantiateRuleHash();
        pool = new ThreadPool(workerThreads);
    }

    public SemiParallelHandler(int workerThreads, Rule rule) {
        super(rule);
        ruleHash = instantiateRuleHash();
        pool = new ThreadPool(workerThreads);
    }

    public SemiParallelHandler(int workerThreads) {
        super();
        ruleHash = instantiateRuleHash();
        pool = new ThreadPool(workerThreads);
    }

    /**
     * Kills the threads of the Handler. This is necessary because they are still waiting for the next call to solve()
     */
    public void kill(){
        pool.kill();
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
            int headerSize = rule.headSize();
            if (temp.containsKey(headerSize)) {
                temp.get(headerSize).add(rule);

            } else {
                List<Rule> ruleList = new ArrayList<>();
                ruleList.add(rule);
                temp.put(headerSize, ruleList);
                headerSizes.add(headerSize);
            }
        }

        return temp;
    }

    @Override
    public boolean addRule(Rule rule) {
        int headerSize = rule.headSize();
        if (ruleHash.containsKey(headerSize)) {
            ruleHash.get(headerSize).add(rule);

        } else {
            List<Rule> ruleList = new ArrayList<>();
            ruleList.add(rule);
            ruleHash.put(headerSize, ruleList);
            headerSizes.add(headerSize);
        }

        return rules.add(rule);
    }

    @Override
    public ConstraintStore solve(ConstraintStore store) {
        if(tracingOn)
            tracer.startMessage(store);

        boolean ruleApplied;
        int cnt = 0;
        while(cnt < 2 && !pool.isTerminated()){
            ruleApplied = true;

            while (ruleApplied) {
                ruleApplied = false;

                for (int size : headerSizes) {
                    if(size <= store.size()){
                        List<Rule> sameHeaderRules = ruleHash.get(size);

                        ruleApplied = applyRule(size, sameHeaderRules, store);
                    }
                }
            }

            if(!pool.isTerminated())
                cnt = 0;
            else
                cnt++;

            System.out.println(cnt);
        }

        while(!pool.isTerminated());

        if (tracingOn)
            tracer.terminatedMessage(store);

        return store;
    }

    private boolean applyRule(int size, List<Rule> sameHeaderRules, ConstraintStore store){
        boolean ruleApplied = false;

        for (Rule rule : sameHeaderRules) {
            int[] selectedIdx = findAssignment(store, rule, new int[size], 0);

            ConstraintStore temp = new ConstraintStore();
            for (int i : selectedIdx) {
                temp.add(store.get(i));
            }

            if(rule.accepts(temp)){
//              Constraint<?>[] before = tracingOn ? temp.getAll().toArray(new Constraint<?>[0]) : null;

                ruleApplied = true;
                store.removeAll(selectedIdx);

                pool.execute(() -> {
                    rule.apply(temp);
                    store.addAll(temp);
                });

//              Constraint<?>[] after = tracingOn ? temp.getAll().toArray(new Constraint<?>[0]) : null;
//              if(tracingOn && !tracer.step(rule, before, after)){
//                  tracer.stopMessage(store);
//                  return store;
//              }
            }
        }

        return ruleApplied;
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
