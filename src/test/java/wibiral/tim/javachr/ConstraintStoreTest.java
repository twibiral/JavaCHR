package wibiral.tim.javachr;

import org.junit.Test;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;

import java.util.List;

import static org.junit.Assert.*;

public class ConstraintStoreTest {

    @Test
    public void clear() {
        ConstraintStore store = new ConstraintStore();
        addTestConstraints(store);
        store.clear();
        assertTrue(store.isEmpty());
    }

    @Test
    public void add() {
        ConstraintStore store = new ConstraintStore();
        store.add(new Constraint<>(42));
        assertEquals(1, store.size());
        assertEquals(new Constraint<>(42), store.remove(0));
        assertTrue(store.isEmpty());
    }

    @Test
    public void testToString() {
        ConstraintStore store = new ConstraintStore();
        addTestConstraints(store);
        System.out.println(store);
        assertTrue(true);
    }

    @Test
    public void isEmpty() {
        ConstraintStore store = new ConstraintStore();
        assertTrue(store.isEmpty());
    }

    @Test
    public void get() {
        ConstraintStore store = new ConstraintStore();
        addTestConstraints(store);
        assertEquals(42, store.get(1).value());
        assertEquals(42, store.get(1).value());
        assertEquals(3.14159, store.get(2).value());
    }

    @Test
    public void getAll() {
        ConstraintStore store = new ConstraintStore();
        addTestConstraints(store);
        List<Constraint<?>> list = store.getAll();
        assertTrue(list.contains(new Constraint<>("Hello World!")));
        assertTrue(list.contains(new Constraint<>(42)));
        assertTrue(list.contains(new Constraint<>(3.14159)));
    }

    @Test
    public void remove() {
        ConstraintStore store = new ConstraintStore();
        addTestConstraints(store);
        assertEquals(42, store.remove(1).value());
        assertEquals(3.14159, store.remove(1).value());
        assertEquals(1, store.size());
    }

    @Test
    public void removeAll() {
        ConstraintStore store = new ConstraintStore();
        addTestConstraints(store);
        store.removeAll(new int[]{0, 2});
        assertEquals(1, store.size());
        assertEquals(42, store.get(0).value());
    }

    void addTestConstraints(ConstraintStore store){
        store.add(new Constraint<>("Hello World!"));
        store.add(new Constraint<>(42));
        store.add(new Constraint<>(3.14159));
    }
}