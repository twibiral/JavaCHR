package javachr.rules;

import org.junit.Before;
import org.junit.Test;
import javachr.constraints.Constraint;

import java.util.List;

import static org.junit.Assert.*;

public class PropagationTest {
    Propagation rule;

    @Before
    public void setUp() {
        rule = new Propagation(1);
        rule.guard(x -> {
            int i = (int) x[0].get();
            return i > 2 && i < 21;
        });
        rule.body((head, newConstraint) -> newConstraint.add(new Constraint<>((int) head[0].get() - 1)));
   }

    @Test
    public void apply() {
        List<Constraint<?>> result = rule.apply(createConstraintArray(3));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(3)));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(2)));
        assertEquals(2, result.size());

        result = rule.apply(createConstraintArray(15));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(14)));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(15)));
        assertEquals(2, result.size());
    }

    @Test
    public void accepts() {
        assertTrue(rule.accepts(createConstraintArray(3)));
        assertTrue(rule.accepts(createConstraintArray(15)));

        assertFalse("Too many constraints, must be false!", rule.accepts(createConstraintArray(12, 7)));
        assertFalse("Too few constraints, must be false!", rule.accepts(createConstraintArray()));

        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(createConstraintArray(42)));
        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(createConstraintArray(-12)));
    }

    @Test
    public void guard() {
        Propagation rule = new Propagation(1).guard(x -> x[0].get().equals(2));
        assertTrue(rule.accepts(createConstraintArray(2)));
        assertFalse(rule.accepts(createConstraintArray(3)));
    }

    @Test
    public void body() {
        Propagation rule = new Propagation(1)
                .body((head, newConstraint) -> newConstraint.add(new Constraint<>((int) head[0].get() + 2)));

        List<Constraint<?>> result = rule.apply(createConstraintArray(2));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(4)));
        assertEquals(2, result.size());
    }

    private Constraint<?>[] createConstraintArray(int... values) {
        Constraint<?>[] constraints = new Constraint<?>[values.length];
        for (int i = 0; i < constraints.length; i++) {
            constraints[i] = new Constraint<>(values[i]);
        }

        return constraints;
    }
}