package javachr.rules;

import org.junit.Before;
import org.junit.Test;
import javachr.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SimpagationTest {
    Simpagation rule;

    @Before
    public void setUp() {
        rule = new Simpagation(1, 1);
        rule.guard((head1, head2) -> {
            int i = (int) head1[0].get();
            int i2 = (int) head2[0].get();
            return i > 2 && i < 21 && i2 == 5;
        });
        rule.body((head1, head2, newConstraints) -> newConstraints.add(new Constraint<>((int) head2[0].get() - 1)));
    }

    @Test
    public void apply() {
        List<Constraint<?>> result = rule.apply(createConstraintList(15, 5));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(15)));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(4)));
        assertFalse(result.stream().anyMatch(c -> c.get().equals(14)));
        assertEquals(2, result.size());
    }

    @Test
    public void accepts() {
        assertTrue(rule.accepts(createConstraintList(3, 5)));
        assertTrue(rule.accepts(createConstraintList(15, 5)));

        assertFalse("Too many constraints, must be false!", rule.accepts(createConstraintList(12, 7, 10)));
        assertFalse("Too few constraints, must be false!", rule.accepts(createConstraintList()));

        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(createConstraintList(5, 42)));
        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(createConstraintList(-12, 5)));
    }

    @Test
    public void guard() {
        Simpagation rule = new Simpagation(2, 2).guard((head1, head2) -> head1[1].get().equals(2));
        assertTrue(rule.accepts(createConstraintList(1, 2, 3, 4)));
    }

    @Test
    public void body() {
        Simpagation rule = new Simpagation(2, 1)
                .body((head1, head2, newConstraint) -> newConstraint.add(new Constraint<>(5)));
        List<Constraint<?>> result = rule.apply(createConstraintList(1, 2, 3));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(1)));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(2)));
        assertTrue(result.stream().anyMatch(c -> c.get().equals(5)));
        assertEquals(3, result.size());
    }

    private List<Constraint<?>> createConstraintList(int... values) {
        List<Constraint<?>> constraints = new ArrayList<>();
        for (int i : values) {
            constraints.add(new Constraint<>(i));
        }
        return constraints;
    }
}