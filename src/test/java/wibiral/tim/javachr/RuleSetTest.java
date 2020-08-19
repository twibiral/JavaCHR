package wibiral.tim.javachr;

import org.junit.Test;
import wibiral.tim.javachr.exceptions.RuleSetIsBlockedException;
import wibiral.tim.javachr.rules.Propagation;

import static org.junit.Assert.*;

public class RuleSetTest {

    @Test
    public void isEmpty() {
        RuleSet rules = new RuleSet();
        assertTrue(rules.isEmpty());
    }

    @Test
    public void add() {
        RuleSet rules = new RuleSet();
        rules.add(new Propagation(1));
        rules.add(new Propagation(2));

        new SimpleSolver(rules);
        try {
            rules.add(new Propagation(1));
            fail("Shouldn't be able to add rules after instantiating solver!");

        } catch (Exception e){
            assertTrue(e instanceof RuleSetIsBlockedException);
        }

        assertEquals(2, rules.size());
    }

    @Test
    public void testToString() {
        RuleSet rules = new RuleSet();
        rules.add(new Propagation(2));
        System.out.println(rules);
    }

    @Test
    public void block() {
        RuleSet rules = new RuleSet();
        rules.add(new Propagation(1));

        new ConstraintSolver(rules) {
            @Override
            public ConstraintStore solve(ConstraintStore store) {
                return store;
            }
        };
        try {
            rules.add(new Propagation(1));
            fail("Shouldn't be able to add rules after instantiating solver!");

        } catch (Exception e){
            assertTrue(e instanceof RuleSetIsBlockedException);
        }
    }

    @Test
    public void size(){
        RuleSet rules = new RuleSet();
        for (int i = 0; i < 200; i++) {
            rules.add(new Propagation(1));
            assertEquals(i+1, rules.size());
        }
    }
}