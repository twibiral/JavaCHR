package wibiral.tim.newjavachr.rules;

import org.junit.Before;
import org.junit.Test;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.rules.Propagation;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PropagationTest {
    Propagation rule1;

    @Before
    public void setUp() {
        rule1 = new Propagation(1);
        rule1.guard(x -> {
            int i = (int) x[0].value();
            return i > 2 && i < 21;
        });
        rule1.body((oldC, newC) -> newC.add(new Constraint<>((int) oldC[0].value() - 1)));
   }

    @Test
    public void apply() {
        assertTrue(rule1.apply(new ConstraintStore(3)));
        ConstraintStore store = new ConstraintStore(15);
        assertTrue(rule1.apply(store));
        assertTrue(store.contains(new Constraint<>(14)));
        assertTrue(store.contains(new Constraint<>(15)));
        assertTrue(store.contains(14));
        assertTrue(store.contains(15));
    }

    @Test
    public void accepts() {
        assertTrue(rule1.accepts(new ConstraintStore(3)));
        assertTrue(rule1.accepts(new ConstraintStore(15)));

        assertFalse("Too many constraints, must be false!", rule1.accepts(new ConstraintStore(12, 7)));
        assertFalse("Too few constraints, must be false!", rule1.accepts(new ConstraintStore()));

        assertFalse("Constraints don't fulfill the conditions, must be false!", rule1.accepts(new ConstraintStore(42)));
        assertFalse("Constraints don't fulfill the conditions, must be false!", rule1.accepts(new ConstraintStore(-12)));


        assertTrue(rule1.accepts(Arrays.asList(new Constraint<?>[]{new Constraint<>(3)})));
        assertTrue(rule1.accepts(Arrays.asList(new Constraint<?>[]{new Constraint<>(15)})));

        assertFalse("Too many constraints, must be false!", rule1.accepts(
                Arrays.asList(new Constraint<?>[]{new Constraint<>(12), new Constraint<>(7)})));
        assertFalse("Too few constraints, must be false!", rule1.accepts(new ArrayList<>()));

        assertFalse("Constraints don't fulfill the conditions, must be false!", rule1.accepts(Arrays.asList(new Constraint<?>[]{new Constraint<>(42)})));
        assertFalse("Constraints don't fulfill the conditions, must be false!", rule1.accepts(Arrays.asList(new Constraint<?>[]{new Constraint<>(-12)})));
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