package javachr;

import javachr.rules.head.Head;
import junit.framework.TestCase;
import javachr.constraints.Constraint;
import javachr.constraints.ConstraintStore;
import javachr.constraints.PropagationHistory;
import javachr.rules.Propagation;
import javachr.rules.Rule;
import javachr.rules.Simpagation;
import javachr.rules.Simplification;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class SimpleRuleApplicatorTest extends TestCase {

    public void testSolve() {
        SimpleRuleApplicator gcd = getGCDSolver();

        // === Call directly ===
        // GCD = 1
        List<Constraint<?>> result = gcd.execute(200, 1);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(1, (int) result.get(0).get());

        // GCD = 17
        result = gcd.execute(680, 34, 85);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(17, (int) result.get(0).get());


        // === Call with constraints ===
        // GCD = 1
        result = gcd.execute(new Constraint<>(200), new Constraint<>(1));
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(1, (int) result.get(0).get());

        // GCD = 17
        result = gcd.execute(new Constraint<>(680), new Constraint<>(34), new Constraint<>(85));
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(17, (int) result.get(0).get());


        // === Call with list of constraints ===
        // GCD = 1
        ArrayList<Constraint<?>> list = new ArrayList<>();
        list.add(new Constraint<>(200)); list.add(new Constraint<>(1));
        result = gcd.execute(list);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(1, (int) result.get(0).get());

        // GCD = 17
        list.clear();
        list.add(new Constraint<>(680)); list.add( new Constraint<>(34)); list.add(new Constraint<>(85));
        result = gcd.execute(list);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(17, (int) result.get(0).get());
    }

    public void testFindMatch() {
        Rule r1 = new Propagation(2).guard(
                head -> head[0].isOfType(Integer.class) && (int)head[0].get() == 42 && head[1].isOfType(String.class)
        );
        Rule r2 = new Simplification(2).guard(  // Rule shouldn't be applicable!
                head -> head[0].isOfType(Integer.class) && head[1].isOfType(Integer.class) && (int)head[0].get() > (int)head[1].get()
        );

        SimpleRuleApplicator solver = new SimpleRuleApplicator(r2, r1);

        SimpleRuleApplicator.RuleAndMatch result = solver.findMatch(
                new ConstraintStore(new Constraint<>("Hello World"), new Constraint<>(42), new Constraint<>(3.14)),
                new PropagationHistory(),
                new ArrayDeque<>(2)
        );

        assertEquals(r1.ID(), result.rule.ID());

        assertTrue(result.match[0].get() instanceof Integer);
        assertTrue(result.match[1].get() instanceof String);

        assertEquals(42, (int) result.match[0].get());
        assertEquals("Hello World", (String) result.match[1].get());
    }

    public void testNoDuplicatesIn() {
        SimpleRuleApplicator solver = new SimpleRuleApplicator();

        Constraint<?>[] array = new Constraint[]{
            new Constraint<>(1), new Constraint<>(2), new Constraint<>("42"), new Constraint<>(3.14)
        };
        assertTrue(solver.noDuplicatesIn(array));

        array[0] = array[2];
        assertFalse(solver.noDuplicatesIn(array));
    }

    public void testNegationAsAbsence() {
        Rule r1 = new Propagation(Integer.class).not(Head.ofType(String.class))
                .body((head, newConstraints) -> newConstraints.add("Wrong rule!"));

        Rule r2 = new Propagation(Integer.class)
                .body((head, newConstraints) -> newConstraints.add("Correct rule!"));

        SimpleRuleApplicator solver = new SimpleRuleApplicator(r1, r2);

        List<Constraint<?>> result = solver.execute(42, "This string prevents rule r1 from being applied!");
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(x -> x.innerObjEquals("Correct rule!")));
        assertFalse(result.stream().anyMatch(x -> x.innerObjEquals("Wrong rule!")));

        result = solver.execute(42);    // Now rule r2 should be applied
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(x -> x.innerObjEquals("Correct rule!")));
        assertTrue(result.stream().anyMatch(x -> x.innerObjEquals("Wrong rule!")));
    }

    private SimpleRuleApplicator getGCDSolver(){
        Rule r1 = new Simpagation(1, 1)
                .guard(
                        (h1, h2) -> (int) h1[0].get() > 0 && (int) h1[0].get() <= (int) h2[0].get()
                ).body(
                        (x1, x2, newConstraints) -> {
                            int n = (int) x1[0].get();
                            int m = (int) x2[0].get();
                            newConstraints.add(new Constraint<>(m - n));
                        }
                );

        // X <=> X=0 | true.
        Rule r2 = new Simplification(1)
                .guard(
                        x -> (int) x[0].get() == 0
                ).body( (x, y) -> {} );

        return new SimpleRuleApplicator(r1, r2);
    }

}