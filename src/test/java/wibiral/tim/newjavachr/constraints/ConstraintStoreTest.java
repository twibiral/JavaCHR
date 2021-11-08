package wibiral.tim.newjavachr.constraints;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConstraintStoreTest {

    @Test
    public void clear() {
        ConstraintStore store = new ConstraintStore();
        addTestConstraints(store);
        store.clear();
        assertEquals(0, store.size());    }

    @Test
    public void add() {
        ConstraintStore store = new ConstraintStore();
        Constraint<?> a = new Constraint<>(42);
        store.add(a);

        assertEquals(1, store.size());
        store.remove(a.ID());
        assertEquals(0, store.size());
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
        assertEquals(0, store.size());    }

    @Test
    public void toList() {
        ConstraintStore store = new ConstraintStore();
        addTestConstraints(store);
        List<Constraint<?>> list = store.toList();
        assertTrue(list.removeIf(x -> x.value().equals(42)));
        assertTrue(list.removeIf(x -> x.value().equals(3.14159)));
        assertTrue(list.removeIf(x -> x.value().toString().equals("Hello World!")));

    }

    @Test
    public void remove() {
        ConstraintStore store = new ConstraintStore();
        long[] IDs = addTestConstraints(store);
        store.remove(IDs[2]);
        store.remove(IDs[0]);
        assertEquals(1, store.size());
    }

    /**
     * Add some test constraints to the store and return an array with their constraints
     */
    long[] addTestConstraints(ConstraintStore store){
        Constraint<?> a = new Constraint<>("Hello World!");
        Constraint<?> b = new Constraint<>(42);
        Constraint<?> c = new Constraint<>(3.14159);

        store.add(a);
        store.add(b);
        store.add(c);

        return new long[]{a.ID(), b.ID(), c.ID()};
    }
}