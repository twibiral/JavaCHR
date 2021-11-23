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
    public void type() {
        Constraint<Integer> c = new Constraint<>(42);
        assertSame(c.type(), Integer.class);

        Constraint<String> c2 = new Constraint<>("Hello World!");
        assertSame(c2.type(), String.class);
    }

    @Test
    public void testIsOfType() {
        Constraint<Integer> c = new Constraint<>(42);
        assertTrue(c.isOfType(Integer.class));

        Constraint<String> c2 = new Constraint<>("Hello World!");
        assertTrue(c2.isOfType(String.class));
    }

    @Test
    public void testToString() {
        Constraint<Integer> c = new Constraint<>(42);
        System.out.println(c);
        assertTrue(true);
    }

    @Test
    public void testID() {
        assertNotEquals(new Constraint<>(123).ID(), new Constraint<>(123).ID());
    }

    @Test
    public void testIsAlive(){
        Constraint<Integer> c = new Constraint<>(42);
        assertTrue(c.isAlive());

        c.setAlive();
        assertTrue(c.isAlive());

        c.setDead();
        assertFalse(c.isAlive());
    }

}