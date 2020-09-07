package wibiral.tim.javachr.constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Holds a set of constraints.
 * Constraints can be added with add({@link Constraint}) method and as constructor parameter.
 */
public class ConstraintStore {
    // TODO: Use faster list implementation when possible
    private final List<Constraint<?>> store = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public ConstraintStore(){   }

    public ConstraintStore(List<Constraint<?>> constraints){
        if(constraints != null)
            store.addAll(constraints);
    }

    public ConstraintStore(Constraint<?>[] constraints){
        if(constraints.length > 0)
            store.addAll(Arrays.asList(constraints));
    }

    @SafeVarargs
    public<T> ConstraintStore(T... values){
        for(T value : values)
            store.add(new Constraint<>(value));
    }

    /**
     * @return true if no constraints are in the store, otherwise false.
     */
    public boolean isEmpty(){
        return store.isEmpty();
    }

    /**
     * Delete all elements in the constraint store.
     */
    public void clear(){
        store.clear();
    }

    /**
     * @param index The index of the constraint that should be returned.
     * @return Returns the constraint at the specified index.
     */
    public Constraint<?> get(int index){
        return store.get(index);
    }

    /**
     * Returns the list that stores all the {@link Constraint}s.
     * Attention: It's the same list as used for storage. Manipulation of the list leads to manipulation
     * of the ConstraintStore.
     * @return A list with all {@link Constraint}s the ConstraintStore holds.
     */
    public List<Constraint<?>> getAll(){
        return store;
    }

    /**
     * Removes and returns the {@link Constraint} at the given index. Shifts any subsequent elements to the left.
     * @param index The index of the Constraint to remove.
     * @return The constraint at the given index.
     */
    public Constraint<?> remove(int index){
        lock.lock();
        Constraint<?> temp = store.remove(index);
        lock.unlock();
        return temp;
    }

    /**
     * Removes all {@link Constraint}s with the given indexes from the store.
     * @param allIndexes An int array that contains the indexes that should be removed.
     * @return true if removing successful.
     */
    public boolean removeAll(int[] allIndexes){
        if(allIndexes.length > store.size())
            return false;

        Arrays.sort(allIndexes);

        lock.lock();

        // Start from behind to avoid problems caused by already removed constraints.
        int old = -1;
        for (int i = allIndexes.length - 1; i >= 0; i--) {
            if(allIndexes[i] >= 0 && allIndexes[i] != old){
                store.remove(allIndexes[i]);
                old = allIndexes[i];
            }
        }

        lock.unlock();

        return true;
    }

    /**
     * Adds the constraint to the end of the constraint store.
     * @param constraint The constraint to add.
     * @return true if added successful, otherwise false.
     */
    public boolean add(Constraint<?> constraint){
        return store.add(constraint);
    }

    /**
     * Add all the constraints in the list to the ConstraintStore.
     * @param constraints The constraints to add.
     * @return True if added successfully.
     */
    public boolean addAll(List<Constraint<?>> constraints){
        return store.addAll(constraints);
    }

    /**
     * Add all the constraints in the ConstraintStore to this ConstraintStore.
     * @param constraintStore The constraints to add.
     * @return True if added successfully.
     */
    public boolean addAll(ConstraintStore constraintStore){
        return store.addAll(constraintStore.getAll());
    }

    /**
     * @param constraint The constraint to check if it's already in the ConstraintStore.
     * @return True if constraint is already in the ConstraintStore.
     */
    public boolean contains(Constraint<?> constraint){
        return store.contains(constraint);
    }

    /**
     * Checks if there is a constraint in this ConstraintStore that contains the given object.
     * @param object The object to check for.
     * @return True if a constraint that contains the object is already in the ConstraintStore.
     */
    public boolean contains(Object object){
        for(Constraint<?> c : store){
            if(c.equals(object))
                return true;
        }

        return false;
    }

    /**
     * @return The number of {@link Constraint}s that are stored.
     */
    public int size(){
        return store.size();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Constraint store:");
        for (Constraint<?> c : store){
            str.append("\n\t").append(c.toString());
        }

        return str.toString();
    }
}
