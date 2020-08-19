package wibiral.tim.javachr;

import org.junit.Test;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Simplification;

import static org.junit.Assert.*;

public class SimpleSolverTest {

    @Test
    public void solve() {

    }

    @Test
    public void findAssignment() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.add(new Simplification(1));
        SimpleSolver solver = new SimpleSolver(ruleSet);

        ConstraintStore store = new ConstraintStore(1, 2, 3, 4);
        Propagation rule1 = new Propagation(1).guard(x -> x[0].type() == Integer.class);
        Propagation rule2 = new Propagation(1).guard(x -> x[0].type() == Integer.class
        && x[1].type() == Integer.class && (int)x[0].value() > 2);
        Propagation rule3 = new Propagation(4).guard(
                x -> (int) x[0].value() == 4 && (int) x[1].value() == 3 && (int) x[2].value() == 2 && (int) x[3].value() == 1
        );

        System.out.println(asString(solver.findAssignment(store, rule1, new int[1], 0)));
        System.out.println(asString(solver.findAssignment(store, rule2, new int[2], 0)));
        System.out.println(asString(solver.findAssignment(store, rule3, new int[4], 0)));
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
        SimpleSolver solver = new SimpleSolver(ruleSet);

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

//    @Test
//    public void possibleAssignments() {
//        RuleSet rules = new RuleSet();
//        rules.add(new Propagation(0));
//        SimpleSolver solver = new SimpleSolver(rules);
//        List<int[]> list = solver.possibleAssignments(4, 4);
//        System.out.println("list.size() = " + list.size());
//
//        for(int[] array : list){
//            for(int i : array)
//                System.out.print(i + " ");
//
//            System.out.println();
//        }
//    }
}