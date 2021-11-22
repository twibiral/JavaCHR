package wibiral.tim.javachr.rules;

import org.junit.Before;
import org.junit.Test;
import wibiral.tim.javachr.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SimplificationTest {
    Simplification rule;

    @Before
    public void setUp() {
        rule = new Simplification(1);
        rule.guard(x -> {
            int i = (int) x[0].value();
            return i > 2 && i < 21;
        });
        rule.body((head, newConstraints) -> newConstraints.add(new Constraint<>((int) head[0].value() - 1)));
    }

    @Test
    public void apply() {
        List<Constraint<?>> result = rule.apply(createConstraintList(3));
        assertTrue(result.stream().anyMatch(c -> c.value().equals(2)));
        assertEquals(1, result.size());

        result = rule.apply(createConstraintList(15));
        assertTrue(result.stream().anyMatch(c -> c.value().equals(14)));
        assertFalse(result.stream().anyMatch(c -> c.value().equals(15)));
        assertEquals(1, result.size());
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
        Simplification rule = new Simplification(1).guard(x -> x[0].value().equals(2));
        assertTrue(rule.accepts(createConstraintList(2)));
    }

    @Test
    public void body() {
        Simplification rule = new Simplification(1)
                .body((oldC, newC) -> newC.add(new Constraint<>(5)));
        List<Constraint<?>> result = rule.apply(createConstraintList(3));
        assertTrue(result.stream().anyMatch(c -> c.value().equals(5)));
        assertEquals(1, result.size());
    }

    private List<Constraint<?>> createConstraintList(int... values) {
        List<Constraint<?>> constraints = new ArrayList<>();
        for (int i : values) {
            constraints.add(new Constraint<>(i));
        }
        return constraints;
    }
}