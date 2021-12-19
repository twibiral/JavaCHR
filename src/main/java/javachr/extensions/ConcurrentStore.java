package javachr.extensions;

import javachr.constraints.Constraint;
import javachr.constraints.ConstraintStore;

import java.util.*;

/**
 * Concurrency safe version of {@link ConstraintStore}.
 * Synchronizes all methods that remove constraints. Doesn't synchronize get or add methods.
 */
public class ConcurrentStore extends ConstraintStore {
    private final List<Constraint<?>> store = new ArrayList<>();

    /**
     * Remove the constraint with the given ID.
     */
    @Override
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
    @Override
    public synchronized void clear(){
        store.forEach(Constraint::setDead);
        store.clear();
    }
}
