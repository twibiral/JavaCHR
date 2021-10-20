package wibiral.tim.newjavachr;

import java.util.Iterator;

/**
 * Implementation of a Constraint Store, oriented toward the definition of the constraint store by
 * Van Weert et al. "CHR for Imperative Host Languages".
 * Stores {@link Constraint}-objects and a rule application history (which rules where applied to which constraints)
 */
public class ConstraintStore {
    /**
     * Add the given constraint to the store.
     */
    public void add(Constraint<?> constraint){

    }

    /**
     * Create a new constraint and add it to the store.
     * @param object the new object to add.
     * @param <T> Type of the given object and the created constraint.
     */
    public <T> void createAndAdd(T object){
        this.add(new Constraint<>(object));
    }

    /**
     * Remove the constraint with the given ID.
     */
    public void remove(long ID){

    }

    /**
     * Returns a complete Iterator for the ConstraintStore.
     * @return An iterator with all elements in the ConstraintStore.
     */
    public Iterator<Constraint<?>> lookup(){
        return null;
    }

    /**
     * Returns an Iterator which contains only the constraints with type constraintType.
     * @param constraintType The type you want the constraints to be.
     * @return An iterator with all elements of type constraintType.
     */
    public Iterator<Constraint<?>> lookup(Class<?> constraintType){
        return null;
    }
}
