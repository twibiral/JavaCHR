package javachr.constraints;

import javachr.RuleApplicator;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generic class that stores an object and an ID. Objects of this class represent CHR constraints and
 * are used by the JavaCHR framework.
 *
 * CONSTRAINTS CAN'T BE INSTANTIATED WITH {@code null}!
 */
public class Constraint<T> {
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);

    private final long ID;
    private final T value;
    private final Class<?> type;

    private boolean alive;

    /**
     * @param value Can't be null! The object that is stored in this constraint.
     */
    public Constraint(T value){
        this.value = value;
        this.type = value.getClass();
        this.alive = true;

        // gives this constraint a new id and increments the id counter for the next constraint.
        ID = ID_COUNTER.getAndIncrement();
    }

    /**
     * @return The Class of the value in this constraint.
     */
    public Class<?> type(){
        return type;
    }

    /**
     * Return the object the constraint holds.
     * @return the stored object.
     */
    public T get(){
        return value;
    }

    /**
     * Checks if the given obj equals the object that is stored in this container.
     * (Simply calls the equals() method of the stored object as parameter)
     * @param obj The object that is compared to the intern object of this Constraint.
     * @return True if the equals() method of the stored object returns true.
     */
    public boolean isEqualTo(Object obj){
        return value.equals(obj);
    }

    /**
     * @return The ID of the Constraint
     */
    public long ID(){
        return ID;
    }

    /**
     * Returns true if the value this constraint contains is of the same class as or a subclass of the given Class parameter.
     * @param type Some class type.
     * @return True if the value of this constraint is assignable to the given type.
     */
    public boolean isOfType(Class<?> type){
        return type.isAssignableFrom(this.type);
    }

    /**
     * @return true if the constraint is still alive. Dead constraints are unregarded by {@link RuleApplicator}s.
     */
    public boolean isAlive(){
        return this.alive;
    }

    /**
     * Switches this constraint to not alive. Dead constraints are unregarded by {@link RuleApplicator}s.
     * Use the method {@link #setAlive()} to revive the constraint.
     */
    public void setDead(){
        this.alive = false;
    }

    /**
     * Switches this constraint to alive. Dead constraints are unregarded by {@link RuleApplicator}s.
     * Use the method {@link #setDead()} to kill the constraint.
     */
    public void setAlive(){
        this.alive = true;
    }

    /**
     * Compares the IDs of two constraints. Returns false if obj is not of type {@link Constraint}.
     * @param obj any object.
     * @return true if the given object is a {@link Constraint} and has the same id as this {@link Constraint}.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        else if(obj instanceof Constraint)
            return ((Constraint<?>) obj).ID() == this.ID();

        else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    @Override
    public String toString() {
        return  "Constraint<" + type.getSimpleName() + ">: " + value;
    }
}



