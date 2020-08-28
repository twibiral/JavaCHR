package wibiral.tim.javachr;

import org.junit.Test;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Simpagation;
import wibiral.tim.javachr.rules.Simplification;

import static org.junit.Assert.*;

public class SimpleHandlerTest {

    @Test
    public void solve() {
        RuleSet rules = getGCDRules();
        ConstraintHandler solver = new SimpleHandler(rules);
        ConstraintStore store;

        store = new ConstraintStore(7, 28);
        solver.solve(store);
        assertEquals(1, store.size());
        assertTrue(store.contains(7));

        store = solver.solve(9, 3);
        assertEquals(1, store.size());
        assertTrue(store.contains(3));
    }

    @Test
    public void findAssignment() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.add(new Simplification(1));
        SimpleHandler solver = new SimpleHandler(ruleSet);

        ConstraintStore store = new ConstraintStore(1, 2, 3, 4);
        Propagation rule1 = new Propagation(1).guard(x -> x[0].type() == Integer.class);
        Propagation rule2 = new Propagation(1).guard(x -> x[0].type() == Integer.class
        && x[1].type() == Integer.class && (int)x[0].value() > 2);
        Propagation rule3 = new Propagation(4).guard(
                x -> (int) x[0].value() == 4 && (int) x[1].value() == 3 && (int) x[2].value() == 2 && (int) x[3].value() == 1
        );

        int[] arr1 = solver.findAssignment(store, rule1, new int[1], 0);
        assertEquals(0, arr1[0]);

        int[] arr2 = solver.findAssignment(store, rule2, new int[2], 0);
        assertEquals(3, arr2[0]);
        assertEquals(3, arr2[1]);

        int[] arr3 = solver.findAssignment(store, rule3, new int[4], 0);
        assertEquals(3, arr3[0]);
        assertEquals(2, arr3[1]);
        assertEquals(1, arr3[2]);
        assertEquals(0, arr3[3]);
    }

    String asString(int[] array){
        StringBuilder str = new StringBuilder();
        for (int i : array) {
            str.append(i);
            str.append(" ");
        }

        return str.toString();
    }

    @Test
    public void notAllEqual() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.add(new Simplification(1));
        SimpleHandler solver = new SimpleHandler(ruleSet);

        int[] array = new int[]{1, 2, 3, 1, 2};
        assertFalse(solver.notAllEqual(array));
        array = new int[]{1, 2, 3, 1, 34, 4};
        assertFalse(solver.notAllEqual(array));
        array = new int[]{2, 3, 1, 2};
        assertFalse(solver.notAllEqual(array));

        array = new int[]{1, 2, 3, 5, 36};
        assertTrue(solver.notAllEqual(array));
        array = new int[]{1};
        assertTrue(solver.notAllEqual(array));
    }


    RuleSet getGCDRules(){
        RuleSet ruleSet = new RuleSet();
        ruleSet.add(new Simpagation(1, 1).guard(
                (h1, h2) ->
                        // h1[0].value() instanceof Integer && h2[0].value() instanceof Integer &&
                        // Not necessary if you can be sure that all Constraints are Integers.
                        (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value()
        ).body(
                (x1, x2, newConstraints) -> {
                    int n = (int) x1[0].value();
                    int m = (int) x2[0].value();
                    newConstraints.add(new Constraint<>(m - n));
                }
        ));
        ruleSet.add(new Simplification(1)
                .guard(
                        x ->
                                // Not necessary if you can be sure that all Constraints are Integers.
                                // x[0].value() instanceof Integer &&
                                (int) x[0].value() == 0
                )
                .body(
                        (x,y) -> {  }
                )
        );

        return ruleSet;
    }
}