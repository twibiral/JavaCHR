package wibiral.tim.newjavachr;

import junit.framework.TestCase;
import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.constraints.ConstraintStore;
import wibiral.tim.newjavachr.rules.Propagation;
import wibiral.tim.newjavachr.rules.Rule;
import wibiral.tim.newjavachr.rules.Simpagation;
import wibiral.tim.newjavachr.rules.Simplification;

import java.util.ArrayList;
import java.util.List;

public class SimpleConstraintSolverTest extends TestCase {

    public void testSolve() {
        SimpleConstraintSolver gcd = getGCDSolver();

        // === Call directly ===
        // GCD = 1
        List<Constraint<?>> result = gcd.solve(200, 1);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(1, (int) result.get(0).value());

        // GCD = 17
        result = gcd.solve(680, 34, 85);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(17, (int) result.get(0).value());


        // === Call with constraints ===
        // GCD = 1
        result = gcd.solve(new Constraint<>(200), new Constraint<>(1));
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(1, (int) result.get(0).value());

        // GCD = 17
        result = gcd.solve(new Constraint<>(680), new Constraint<>(34), new Constraint<>(85));
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(17, (int) result.get(0).value());


        // === Call with list of constraints ===
        // GCD = 1
        ArrayList<Constraint<?>> list = new ArrayList<>();
        list.add(new Constraint<>(200)); list.add(new Constraint<>(1));
        result = gcd.solve(list);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(1, (int) result.get(0).value());

        // GCD = 17
        list.clear();
        list.add(new Constraint<>(680)); list.add( new Constraint<>(34)); list.add(new Constraint<>(85));
        result = gcd.solve(list);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOfType(Integer.class));
        assertEquals(17, (int) result.get(0).value());
    }

    public void testFindMatch() {
        Rule r1 = new Propagation(2).guard(
                head -> head[0].isOfType(Integer.class) && (int)head[0].value() == 42 && head[1].isOfType(String.class)
        );
        Rule r2 = new Simplification(2).guard(  // Rule shouldn't be applicable!
                head -> head[0].isOfType(Integer.class) && head[1].isOfType(Integer.class) && (int)head[0].value() > (int)head[1].value()
        );

        SimpleConstraintSolver solver = new SimpleConstraintSolver(r2, r1);

        SimpleConstraintSolver.RuleAndMatch result = solver.findMatch(
                new ConstraintStore(new Constraint<>("Hello World"), new Constraint<>(42), new Constraint<>(3.14))
        );

        assertEquals(r1.ID(), result.rule.ID());

        assertTrue(result.match[0].value() instanceof Integer);
        assertTrue(result.match[1].value() instanceof String);

        assertEquals(42, (int) result.match[0].value());
        assertEquals("Hello World", (String) result.match[1].value());
    }

    public void testNoDuplicatesIn() {
        SimpleConstraintSolver solver = new SimpleConstraintSolver();

        Constraint<?>[] array = new Constraint[]{
            new Constraint<>(1), new Constraint<>(2), new Constraint<>("42"), new Constraint<>(3.14)
        };
        assertTrue(solver.noDuplicatesIn(array));

        array[0] = array[2];
        assertFalse(solver.noDuplicatesIn(array));
    }

    private SimpleConstraintSolver getGCDSolver(){
        Rule r1 = new Simpagation(1, 1)
                .guard(
                        (h1, h2) -> (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value()
                ).body(
                        (x1, x2, newConstraints) -> {
                            int n = (int) x1[0].value();
                            int m = (int) x2[0].value();
                            newConstraints.add(new Constraint<>(m - n));
                        }
                );

        // X <=> X=0 | true.
        Rule r2 = new Simplification(1)
                .guard(
                        x -> (int) x[0].value() == 0
                ).body( (x, y) -> {} );

        return new SimpleConstraintSolver(r1, r2);
    }

}