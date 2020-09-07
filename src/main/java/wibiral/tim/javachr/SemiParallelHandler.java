package wibiral.tim.javachr;

import wibiral.tim.javachr.constraints.Constraint;
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

        boolean ruleApplied;
        int cnt = 0;
        while (cnt < 2 || !pool.isTerminated()) {
            ruleApplied = true;

            while (ruleApplied) {
                ruleApplied = false;

                for (int size : headerSizes) {
                    if (size <= store.size()) {
                        List<Rule> sameHeaderRules = ruleHash.get(size);

                        ruleApplied = applyRule(size, sameHeaderRules, store);
                    }
                }
            }

            if (!pool.isTerminated())
                cnt = 0;
            else
                cnt++;

        }

        while (!pool.isTerminated())
            System.out.println("Not terminated!");

        pool.block();

        if (tracingOn)
            tracer.terminatedMessage(store);

        return store;
    }

    private boolean applyRule(int size, List<Rule> sameHeaderRules, ConstraintStore store) {
        boolean ruleApplied = false;

        for (Rule rule : sameHeaderRules) {
            int[] selectedIdx = findAssignment(store, rule, new int[size], 0);

            ConstraintStore temp = new ConstraintStore();
            for (int i : selectedIdx) {
                temp.add(store.get(i));
            }

            if (rule.accepts(temp)) {
                Constraint<?>[] before = tracingOn ? temp.getAll().toArray(new Constraint<?>[0]) : null;

                ruleApplied = true;
                store.removeAll(selectedIdx);

                pool.execute(() -> {
                    rule.apply(temp);
                    store.addAll(temp);
                });

                Constraint<?>[] after = tracingOn ? temp.getAll().toArray(new Constraint<?>[0]) : null;
                if (tracingOn && !tracer.step(rule, before, after)) {
                    tracer.stopMessage(store);
                }
            }
        }

        return ruleApplied;
    }
}
