package wibiral.tim.newjavachr;

import java.util.concurrent.atomic.AtomicLong;

public class Constraint<T> {
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);

    private final long ID;
    private T value;
    private boolean alive;

    public Constraint(T value){
        this.value = value;
        this.alive = true;

        // gives this constraint a new id and increments the id counter for the next constraint.
        ID = ID_COUNTER.getAndIncrement();
    }

    /**
     * @return The Class of the value in this constraint.
     */
    public Class<?> type(){
        return value.getClass();
    }

    /**
     * Return the value the constraint holds.
     * @return value.
     */
    public T value(){
        return value;
    }

    public long ID(){
        return ID;
    }

    /**
     * Returns true if the value this constraint contains is of the same class as or a subclass of the given Class parameter.
     * @param type Some class type.
     * @return True if the value of this constraint is assignable to the given type.
     */
    public boolean isOfType(Class<?> type){
        return type.isAssignableFrom(this.type());
    }

    /**
     * @return true if the constraint is still alive. Dead constraints are unregarded by {@link ConstraintSolver}s.
     */
    public boolean isAlive(){
        return this.alive;
    }

    /**
     * Switches this constraint to not alive. Dead constraints are unregarded by {@link ConstraintSolver}s.
     * Use the method {@link #setAlive()} to revive the constraint.
     */
    public void setDead(){
        this.alive = false;
    }

    /**
     * Switches this constraint to alive. Dead constraints are unregarded by {@link ConstraintSolver}s.
     * Use the method {@link #setDead()} to kill the constraint.
     */
    public void setAlive(){
        this.alive = true;
    }

//    /**
//     * PROBLEM: Maybe during concurrent there are problems with inconsistency.
//     *
//     * Instead of deleting an old constraint and creating a new one this method allows you to update an existing constraint.
//     * This way is more efficient.
//     * @param newValue The new value that is assigned to the constraint.
//     */
//    public void update(T newValue){
//        value = newValue;
//        id = ID_COUNTER.getAndIncrement();
//    }

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
    public String toString() {
        return  "Constraint<" + value.getClass().getSimpleName() + ">: " + value;
    }
}



