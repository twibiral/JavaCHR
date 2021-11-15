package wibiral.tim.newjavachr.constraints;

import java.util.*;


/**
 * Implementation of a Constraint Store, oriented toward the definition of the constraint store by
 * Van Weert et al. "CHR for Imperative Host Languages".
 * Stores {@link Constraint}-objects and a rule application history (which rules where applied to which constraints)
 */
public class ConstraintStore {
    private final List<Constraint<?>> store = new ArrayList<>();

    public ConstraintStore(Collection<Constraint<?>> constraints) {
        if(constraints == null)
            return;

        List<Constraint<?>> withoutDuplicates = new ArrayList<>(new HashSet<>(constraints));
        store.addAll(withoutDuplicates);
        store.forEach(Constraint::setAlive);
    }

    public ConstraintStore(Constraint<?>... constraints) {
        if (constraints.length <= 0)
            return;

        List<Constraint<?>> withoutDuplicates = new ArrayList<>(new HashSet<>(Arrays.asList(constraints)));
        store.addAll(withoutDuplicates);
        store.forEach(Constraint::setAlive);
    }

    @SafeVarargs
    public <T> ConstraintStore(T... values) {
        for (T value : values)
            this.add(new Constraint<>(value));
    }

    /**
     * Add the given constraint to the store.
     */
    public void add(Constraint<?> constraint){
        store.add(constraint);
        // All rules in the store must be alive if they aren't in use:
        constraint.setAlive();
    }

    /**
     * Adds all the constraints of the list to the constraint store.
     * @param constraints Constraints that get added to the store.
     */
    public void addAll(Collection<Constraint<?>> constraints){
        List<Constraint<?>> withoutDuplicates = new ArrayList<>(new HashSet<>(constraints));
        store.addAll(withoutDuplicates);
        // All rules in the store must be alive if they aren't in use:
        withoutDuplicates.forEach(Constraint::setAlive);
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
        store.forEach(x -> {
            if(x.ID() == ID){
                x.setDead();
            }
        });
        store.removeIf(x -> x.ID() == ID);
    }

    /**
     * Returns a complete Iterator for the ConstraintStore.
     * @return An iterator with all elements in the ConstraintStore.
     */
    public Iterator<Constraint<?>> lookup(){
        return store.iterator();
    }

    /**
     * Returns an Iterator which contains only the constraints with type constraintType.
     * @param constraintType The type you want the constraints to be.
     * @return An iterator with all elements of type constraintType.
     */
    public Iterator<Constraint<?>> lookup(Class<?> constraintType){
        return store.stream().filter(x -> x.isOfType(constraintType) && x.isAlive()).iterator();
    }

    /**
     * Set all constraints to dead and remove them from the internal data structure.
     */
    public void clear(){
        store.forEach(Constraint::setDead);
        store.clear();
    }

    public int size(){
        return store.size();
    }

    public List<Constraint<?>> toList(){
        return new ArrayList<>(store);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("ConstraintStore:");
        for (Constraint<?> c : store) {
            str.append("\n\t").append(c.toString());
        }

        return str.toString();
    }
}
