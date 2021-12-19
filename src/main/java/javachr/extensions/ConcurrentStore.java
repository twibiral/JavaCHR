package javachr.extensions;

import javachr.constraints.Constraint;
import javachr.constraints.ConstraintStore;

import java.util.*;

/**
 * Concurrency safe version of {@link ConstraintStore}.
 * Synchronizes all methods that add or remove constraints. Doesn't synchronize get methods.
 */
public class ConcurrentStore extends ConstraintStore {
    private final List<Constraint<?>> store = new ArrayList<>();

    /**
     * Add the given constraint to the store.
     */
    public void add(Constraint<?> constraint){
        store.add(constraint);
        // All rules in the store must be alive if they aren't in use:
        constraint.setAlive();
    }

    /**
     * Create a new constraint and add it to the store.
     * @param object the new object to add.
     * @param <T> Type of the given object and the created constraint.
     */
    public synchronized <T> void add(T object){
        this.add(new Constraint<>(object));
    }

    /**
     * Adds all the constraints of the list to the constraint store.
     * @param collection Constraints that get added to the store.
     */
    public void addAll(Collection<Constraint<?>> collection){
        HashSet<Constraint<?>> withoutDuplicates = new HashSet<>(collection);

        synchronized (this) {
            store.addAll(withoutDuplicates);
        }

        // All rules in the store must be alive if they aren't in use:
        withoutDuplicates.forEach(Constraint::setAlive);
    }

    /**
     * Adds all the constraints of the list to the constraint store.
     * @param constraints Constraints that get added to the store.
     */
    public void addAll(Constraint<?>... constraints){
        HashSet<Constraint<?>> withoutDuplicates = new HashSet<>(Arrays.asList(constraints));

        synchronized (this) {
            store.addAll(withoutDuplicates);
        }

        // All rules in the store must be alive if they aren't in use:
        withoutDuplicates.forEach(Constraint::setAlive);
    }

    /**
     * Remove the constraint with the given ID.
     */
    public synchronized void remove(long ID){
        store.forEach(x -> {
            if(x.getID() == ID){
                x.setDead();
            }
        });
        store.removeIf(x -> x.getID() == ID);
    }

    /**
     * Set all constraints to dead and remove them from the internal data structure.
     */
    public synchronized void clear(){
        store.forEach(Constraint::setDead);
        store.clear();
    }
}
