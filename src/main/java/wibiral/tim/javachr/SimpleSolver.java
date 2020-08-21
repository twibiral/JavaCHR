package wibiral.tim.javachr;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleSolver extends ConstraintSolver {
    /**
     * Contains for all header the rules have a List with all rules with that specific header size.
     */
    private final HashMap<Integer, List<Rule>> ruleHash;

    private final List<Integer> headerSizes = new ArrayList<>();

    public SimpleSolver(RuleSet rules) {
        super(rules);
        ruleHash = new HashMap<>(3 * rules.size());

        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
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
    }

    @Override
    public ConstraintStore solve(ConstraintStore store) {
        int noRuleApplied = 0;
        while (noRuleApplied < store.size()) {

            for (int size : headerSizes) {
                if(size <= store.size()){
                    List<Rule> sameHeaderRules = ruleHash.get(size);

                    for (Rule rule : sameHeaderRules) {
                        int[] selectedIdx = findAssignment(store, rule, new int[size], 0);

                        ConstraintStore temp = new ConstraintStore();
                        for (int i : selectedIdx) {
                            // Start from the end to avoid index problems
                            temp.add(store.get(i));
                        }

                        if(rule.accepts(temp)){
                            store.removeAll(selectedIdx);

                            rule.apply(temp);
                            store.addAll(temp);

                        } else {
                            noRuleApplied++;
                        }
                    }

                }
            }

        }
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
