package wibiral.tim.javachr.constraints;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConstraintTest {
    @Test
    public void value() {
        Double pi = 3.14159;
        Constraint<Double> c = new Constraint<>(pi);
        assertNotNull(c.value());
        Double d = c.value();
        assertEquals(pi, d);
    }

    @Test
    public void testEquals() {
        Constraint<Character> c1 = new Constraint<>('a');
        Constraint<Character> c2 = new Constraint<>('a');
        assertTrue(c1.equals(c2));
        assertTrue(c1.equals('a'));

        assertFalse(c1.equals(null));
        assertFalse(c1.equals('y'));

        ConstraintStore store = new ConstraintStore();
        store.add(c1);
        store.add(c2);
    }

    @Test
    public void size(){
        ConstraintStore store = new ConstraintStore();
        for (int i = 0; i < 200; i++) {
            store.add(new Constraint<>(i*3));
            assertEquals(i+1, store.size());
        }
    }

    @Test
    public void type() {
        Constraint<Integer> c = new Constraint<>(42);
        assertSame(c.type(), Integer.class);

        Constraint<String> c2 = new Constraint<>("Hello World!");
        assertSame(c2.type(), String.class);
    }

    @Test
    public void testToString() {
        Constraint<Integer> c = new Constraint<>(42);
        System.out.println(c);
    }
}