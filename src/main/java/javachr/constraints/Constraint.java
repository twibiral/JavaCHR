package javachr.constraints;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generic class that stores an object and an ID. Objects of this class represent CHR constraints and
 * are used by the JavaCHR framework.
 *
 * CONSTRAINTS CAN'T BE INSTANTIATED WITH {@code null}!
 */
public class Constraint<T> {
    private static final AtomicLong ID_COUNTER = new AtomicLong(1);

    private final long ID;
    private final T value;
    private final Class<?> type;


    /**
     * @param value Can't be null! The object that is stored in this constraint.
     */
    public Constraint(T value){
        this.value = value;
        this.type = value.getClass();

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
    public boolean innerObjEquals(Object obj){
        return value.equals(obj);
    }

    /**
     * @return The ID of the Constraint
     */
    public long getID(){
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
     * Compares the IDs of two constraints. Returns false if obj is not of type {@link Constraint}.
     * @param obj any object.
     * @return true if the given object is a {@link Constraint} and has the same id as this {@link Constraint}.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        else if(obj instanceof Constraint)
            return ((Constraint<?>) obj).getID() == this.getID();

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



