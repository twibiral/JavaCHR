package wibiral.tim.javachr.rules;

import org.junit.Before;
import org.junit.Test;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class SimpagationTest {
    Simpagation rule;

    @Before
    public void setUp() {
        rule = new Simpagation(1, 1);
        rule.guard((x1, x2) -> {
            int i = (int) x1[0].value();
            int i2 = (int) x2[0].value();
            return i > 2 && i < 21 && i2 == 5;
        });
        rule.body((old1, old2, newC) -> newC.add(new Constraint<>((int) old2[0].value() - 1)));
    }

    @Test
    public void apply() {
        assertTrue(rule.apply(new ConstraintStore(3, 5)));
        ConstraintStore store = new ConstraintStore(15, 5);
        assertTrue(rule.apply(store));
        System.out.println(store);
        assertTrue(store.contains(new Constraint<>(15)));
        assertTrue(store.contains(15));
        assertTrue(store.contains(4));
        assertFalse(store.contains(new Constraint<>(5)));
    }

    @Test
    public void accepts() {
        assertTrue(rule.accepts(new ConstraintStore(3, 5)));
        assertTrue(rule.accepts(new ConstraintStore(15, 5)));

        assertFalse("Too many constraints, must be false!", rule.accepts(new ConstraintStore(12, 7, 5)));
        assertFalse("Too few constraints, must be false!", rule.accepts(new ConstraintStore()));

        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(new ConstraintStore(42, 7)));
        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(new ConstraintStore(-12, 323)));


        assertTrue(rule.accepts(Arrays.asList(new Constraint<?>[]{new Constraint<>(3), new Constraint<>(5)})));
        assertTrue(rule.accepts(Arrays.asList(new Constraint<?>[]{new Constraint<>(15), new Constraint<>(5)})));

        assertFalse("Too many constraints, must be false!", rule.accepts(
                Arrays.asList(new Constraint<?>[]{new Constraint<>(12), new Constraint<>(7)})));
        assertFalse("Too few constraints, must be false!", rule.accepts(new ArrayList<>()));

        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(Arrays.asList(new Constraint<?>[]{new Constraint<>(42)})));
        assertFalse("Constraints don't fulfill the conditions, must be false!", rule.accepts(Arrays.asList(new Constraint<?>[]{new Constraint<>(-12)})));
    }

    @Test
    public void guard() {
        Propagation rule = new Propagation(1).guard(x -> x[0].value().equals(2));
        assertTrue(rule.accepts(new ConstraintStore(2)));
    }

    @Test
    public void body() {
        Propagation rule = new Propagation(1).body((oldC, newC) -> newC.add(new Constraint<>((int) oldC[0].value() + 2)));
        ConstraintStore s = new ConstraintStore(2);
        assertTrue(rule.apply(s));
        assertTrue(s.contains(new Constraint<>(4)));
    }
}