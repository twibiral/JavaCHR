package wibiral.tim.javachr;

import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.tracing.CommandLineTracer;
import wibiral.tim.javachr.tracing.Tracer;

import java.util.List;

public class SemiParallelHandler extends SimpleHandler {
    private final ThreadPool pool;

    private Tracer tracer;
    private boolean tracingOn = false;

    public SemiParallelHandler(int workerThreads, Rule... rules) {
        super(rules);
        pool = new ThreadPool(workerThreads);
    }

    public SemiParallelHandler(int workerThreads, Rule rule) {
        super(rule);
        pool = new ThreadPool(workerThreads);
    }

    public SemiParallelHandler(int workerThreads) {
        super();
        pool = new ThreadPool(workerThreads);
    }

    /**
     * Kills the threads of the Handler. This is necessary because they are still waiting for the next call to solve()
     */
    public void kill() {
        pool.kill();
    }

    @Override
    public boolean trace() {
        tracingOn = !tracingOn;
        tracer = tracer == null ? new CommandLineTracer() : tracer;
        return tracingOn;
    }

    @Override
    public ConstraintStore solve(ConstraintStore store) {
        if (tracingOn)
            tracer.startMessage(store);

        boolean anyRuleApplied = true;
        while (anyRuleApplied || !pool.isTerminated()) {
            anyRuleApplied = false;

            boolean ruleApplied = true;
            while (ruleApplied) {
                ruleApplied = false;

                for (int size : headerSizes) {
                    if (size <= store.size()) {
                        List<Rule> sameHeaderRules = ruleHash.get(size);

                        ruleApplied = ruleApplied || applyRule(size, sameHeaderRules, store) || ruleApplied;
                        anyRuleApplied = ruleApplied || anyRuleApplied;
                    }
                }

            }

            pool.awaitTermination();
        }

        pool.block();

        if (tracingOn)
            tracer.terminatedMessage(store);

        return store;
    }

    private boolean applyRule(int size, List<Rule> sameHeaderRules, ConstraintStore store) {
        for (Rule rule : sameHeaderRules) {
            int[] selectedIdx = findAssignment(store, rule, new int[size], 0);

            ConstraintStore temp = new ConstraintStore();
            for (int i : selectedIdx) {
                temp.add(store.get(i));
            }

            if (rule.accepts(temp)) {
                store.removeAll(selectedIdx);

                pool.execute(() -> {
                    rule.apply(temp);
                    store.addAll(temp);
                });

                return true;
            }
        }

        return false;
    }
}
