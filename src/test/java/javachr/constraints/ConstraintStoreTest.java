package javachr.constraints;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

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
        store.remove(a.getID());
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
        assertTrue(list.removeIf(x -> x.get().equals(42)));
        assertTrue(list.removeIf(x -> x.get().equals(3.14159)));
        assertTrue(list.removeIf(x -> x.get().toString().equals("Hello World!")));

    }

    @Test
    public void remove() {
        ConstraintStore store = new ConstraintStore();
        long[] IDs = addTestConstraints(store);
        store.remove(IDs[2]);
        store.remove(IDs[0]);
        assertEquals(1, store.size());
    }

    @Test
    public void addAll() {
        Constraint<?> a = new Constraint<>("Hello World!");
        Constraint<?> b = new Constraint<>(42);
        Constraint<?> c = new Constraint<>(3.141);

        ConstraintStore store = new ConstraintStore();
        store.addAll(Arrays.asList(a, b, c));

        assertEquals(3, store.size());
        assertTrue(store.toList().contains(a));
        assertTrue(store.toList().contains(b));
        assertTrue(store.toList().contains(c));

        store = new ConstraintStore();
        store.addAll(a, b, c);

        assertEquals(3, store.size());
        assertTrue(store.toList().contains(a));
        assertTrue(store.toList().contains(b));
        assertTrue(store.toList().contains(c));
    }

    @Test
    public void createAndAdd() {
        ConstraintStore store = new ConstraintStore();
        store.add("Hello World!");
        store.add(42);
        store.add(3.141);

        assertEquals(3, store.size());
        assertEquals(1, store.toList().stream().filter(x -> x.get().equals("Hello World!")).count());
        assertTrue(store.toList().stream().anyMatch(x -> x.get().equals(42)));
        assertTrue(store.toList().stream().anyMatch(x -> x.get().equals(3.141)));
    }

    @Test
    public void lookup() {
        Constraint<?> a = new Constraint<>("Hello World!");
        Constraint<?> b = new Constraint<>(42);
        Constraint<?> c = new Constraint<>(3.141);
        Constraint<?> d = new Constraint<>("42");

        ConstraintStore store = new ConstraintStore();
        store.addAll(Arrays.asList(a, b, c, d));

        Iterator<Constraint<?>> iter = store.lookup();
        List<Constraint<?>> list = new ArrayList<Constraint<?>>();
        iter.forEachRemaining(list::add);
        assertTrue(list.contains(a));
        assertTrue(list.contains(b));
        assertTrue(list.contains(c));
        assertTrue(list.contains(d));
        assertEquals(4, list.size());

        iter = store.lookup(Integer.class);
        list.clear();
        iter.forEachRemaining(list::add);
        assertTrue(list.contains(b));
        assertEquals(1, list.size());

        iter = store.lookup(String.class);
        list.clear();
        iter.forEachRemaining(list::add);
        assertTrue(list.contains(a));
        assertTrue(list.contains(d));
        assertEquals(2, list.size());

        // Dont use float! Java uses double by default!
        iter = store.lookup(Double.class);
        list.clear();
        iter.forEachRemaining(list::add);
        assertTrue(list.contains(c));
        assertEquals(1, list.size());

        iter = store.lookup("Hello World!");
        list.clear();
        iter.forEachRemaining(list::add);
        assertTrue(list.contains(a));
        assertEquals(1, list.size());

        iter = store.lookup(42);
        list.clear();
        iter.forEachRemaining(list::add);
        assertTrue(list.contains(b));
        assertEquals(1, list.size());

        iter = store.lookup(3.141);
        list.clear();
        iter.forEachRemaining(list::add);
        assertTrue(list.contains(c));
        assertEquals(1, list.size());

        iter = store.lookup("42");
        list.clear();
        iter.forEachRemaining(list::add);
        assertTrue(list.contains(d));
        assertEquals(1, list.size());
    }

    @Test
    public void size() {
        ConstraintStore store = new ConstraintStore();
        addTestConstraints(store);
        assertEquals(3, store.size());

        store.add("42");
        assertEquals(4, store.size());
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

        return new long[]{a.getID(), b.getID(), c.getID()};
    }
}