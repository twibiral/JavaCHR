package wibiral.tim.javachr.rules;

import org.junit.Before;
import org.junit.Test;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PropagationTest {
    Propagation rule;

    @Before
    public void setUp() {
        rule = new Propagation(1);
        rule.guard(x -> {
            int i = (int) x[0].value();
            return i > 2 && i < 21;
        });
        rule.body((head, newConstraint) -> newConstraint.add(new Constraint<>((int) head[0].value() - 1)));
   }

    @Test
    public void apply() {
        List<Constraint<?>> result = rule.apply(createConstraintList(3));
        assertTrue(result.stream().anyMatch(c -> c.value().equals(3)));
        assertTrue(result.stream().anyMatch(c -> c.value().equals(2)));
        assertEquals(2, result.size());

        result = rule.apply(createConstraintList(15));
        assertTrue(result.stream().anyMatch(c -> c.value().equals(14)));
        assertTrue(result.stream().anyMatch(c -> c.value().equals(15)));
        assertEquals(2, result.size());
    }

    @Test
    public void accepts() {
        assertTrue(rule.accepts(createConstraintList(3)));
        assertTrue(rule.accepts(createConstraintList(15)));

        assertFalse("Too many constraints, must be false!", rule.accepts(createConstraintList(12, 7)));
        assertFalse("Too few constraints, must be false!", rule.accepts(createConstraintList()));

        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(createConstraintList(42)));
        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(createConstraintList(-12)));
    }

    @Test
    public void guard() {
        Propagation rule = new Propagation(1).guard(x -> x[0].value().equals(2));
        assertTrue(rule.accepts(createConstraintList(2)));
        assertFalse(rule.accepts(createConstraintList(3)));
    }

    @Test
    public void body() {
        Propagation rule = new Propagation(1)
                .body((head, newConstraint) -> newConstraint.add(new Constraint<>((int) head[0].value() + 2)));
        List<Constraint<?>> result = rule.apply(createConstraintList(2));
        assertTrue(result.stream().anyMatch(c -> c.value().equals(4)));
        assertEquals(2, result.size());
    }

    private List<Constraint<?>> createConstraintList(int... values) {
        List<Constraint<?>> constraints = new ArrayList<>();
        for (int i : values) {
            constraints.add(new Constraint<>(i));
        }
        return constraints;
    }
}