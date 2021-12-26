package javachr.extensions;

import javachr.constraints.ConstraintStore;

/**
 * Concurrency safe version of {@link ConstraintStore}.
 * Synchronizes all methods that remove constraints. Doesn't synchronize get or add methods.
 */
public class ConcurrentStore extends ConstraintStore {
    /**
     * Remove the constraint with the given ID.
     */
    @Override
    public synchronized void remove(long ID){
        deadConstraints.add(ID);
        store.removeIf(x -> x.getID() == ID);
    }

    /**
     * Set all constraints to dead and remove them from the internal data structure.
     */
    @Override
    public synchronized void clear(){
        store.forEach(x -> setDead(x.getID()));
        store.clear();
    }
}
