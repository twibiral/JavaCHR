package javachr.constraints;

import javachr.RuleApplicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


/**
 * Implementation of a Constraint Store, oriented toward the definition of the constraint store by
 * Van Weert et al. "CHR for Imperative Host Languages".
 * Stores {@link Constraint}-objects and a rule application history (which rules where applied to which constraints)
 */
public class ConstraintStore {
    private final List<Constraint<?>> store = new ArrayList<>();
    private final HashSet<Long> deadConstraints = new HashSet<>();


    public ConstraintStore() {
        // Empty constructor for empty constraint stores.
    }


    public ConstraintStore(Collection<Constraint<?>> constraints) {
        if(constraints == null)
            return;

        List<Constraint<?>> withoutDuplicates = new ArrayList<>(new HashSet<>(constraints));
        store.addAll(withoutDuplicates);
    }


    public ConstraintStore(Constraint<?>... constraints) {
        if (constraints.length <= 0)
            return;

        List<Constraint<?>> withoutDuplicates = new ArrayList<>(new HashSet<>(Arrays.asList(constraints)));
        store.addAll(withoutDuplicates);
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
        setAlive(constraint.getID());
    }


    /**
     * Create a new constraint and add it to the store.
     * @param object the new object to add.
     * @param <T> Type of the given object and the created constraint.
     */
    public <T> void add(T object){
        this.add(new Constraint<>(object));
    }


    /**
     * Adds all the constraints of the list to the constraint store.
     * @param collection Constraints that get added to the store.
     */
    public void addAll(Collection<Constraint<?>> collection){
        HashSet<Constraint<?>> withoutDuplicates = new HashSet<>(collection);
        store.addAll(withoutDuplicates);
        withoutDuplicates.forEach(x -> setAlive(x.getID()));
    }


    /**
     * Adds all the constraints of the list to the constraint store.
     * @param constraints Constraints that get added to the store.
     */
    public void addAll(Constraint<?>... constraints){
        HashSet<Constraint<?>> withoutDuplicates = new HashSet<>(Arrays.asList(constraints));
        store.addAll(withoutDuplicates);
        withoutDuplicates.forEach(x -> setAlive(x.getID()));
    }


    /**
     * Remove the constraint with the given ID.
     */
    public void remove(long ID){
        deadConstraints.add(ID);
        store.removeIf(x -> x.getID() == ID);
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
     * @return An iterator with all elements of type {@param constraintType}.
     */
    public Iterator<Constraint<?>> lookup(Class<?> constraintType){
        return store.stream().filter(x -> isAlive(x.getID()) && x.isOfType(constraintType)).iterator();
    }


    /**
     * Returns an iterator which contains only the constraints that contain an object that is equal to {@param value}
     * Equivalence is determined by the function by calling .equal() on the object in the constraint with the {@param value}
     * as parameter.
     * @param value The value that all constraints in the iterator should contain. (e.g. String "42")
     * @return An iterator with constraints that contain objects equal to {@param value}.
     */
    public Iterator<Constraint<?>> lookup(Object value){
        return store.stream()
                .filter(x -> isAlive(x.getID()) && x.innerObjEquals(value))
                .iterator();
    }


    /**
     * @return The first constraint in the store that is alive.
     */
    public Constraint<?> getFirst(){
        return store.stream()
                .filter(x -> isAlive(x.getID()))
                .findFirst()
                .orElse(null);
    }


    /**
     * @return The last constraint in the store that is alive.
     */
    public Constraint<?> getLast(){
        return store.stream()
                .filter(x -> isAlive(x.getID()))
                .reduce((x, y) -> y)
                .orElse(null);
    }


    /**
     * Sets the constraint with the given ID to dead if the constraint exists in the internal store.
     * Dead constraints are unregarded by {@link RuleApplicator}s.
     *
     * @param ID the ID of the constraint that should be set to dead.
     */
    public void setDead(long ID){
        deadConstraints.add(ID);
    }


    /**
     * Sets the constraint with the given ID to alive.
     * Removes the ID from the list of dead constraints if it is in there.
     * Dead constraints are unregarded by {@link RuleApplicator}s.
     *
     * @param ID The ID of the constraint that should be set to alive.
     */
    public void setAlive(long ID){
        deadConstraints.remove(ID);
    }


    public boolean isAlive(long ID){
        return !deadConstraints.contains(ID);
    }


    /**
     * Cleans the internal store of dead constraints.
     * Removes all stored IDs that refer to constraints that are no longer in the internal data structure.
     */
    public void cleanup() {
        deadConstraints.removeIf(x -> !store.stream().anyMatch(y -> y.getID() == x));
    }


    /**
     * Set all constraints to dead and remove them from the internal data structure.
     */
    public void clear(){
        store.forEach(x -> setDead(x.getID()));
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