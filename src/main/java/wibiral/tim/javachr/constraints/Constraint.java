package wibiral.tim.javachr.constraints;

import wibiral.tim.javachr.exceptions.AlreadyBoundException;

public class Constraint<T> {
    private boolean isBound = false;
    private T value = null;

    public Constraint(){

    }

    public Constraint(T value){
        if(value != null){
            this.value = value;
            isBound = true;
        }
    }

    /**
     * @return The Class of the value in this constraint.
     */
    public Class<?> type(){
        return value.getClass();
    }

    /**
     * Binds a new value to the constraint unless the constraint is already bound to a value.
     * @param value Binds the constraint to this value.
     * @return true if binding was successful, throws {@link AlreadyBoundException} if the constraint is already bound!
     */
    public boolean bind(T value){
        if(isBound){
            throw new AlreadyBoundException("Tried to bind new value to constraint that is already bound!");

        } else {
            isBound = true;
            this.value = value;
            return true;
        }
    }

    /**
     * Unbinds the constraint from its value and returns the old value.
     * @return The old value.
     */
    public T unbind(){
        if(isBound){
            isBound = false;
            return value;

        } else {
            return null;
        }
    }

    /**
     * Return the value the constraint holds.
     * @return value.
     */
    public T value(){
        return value;
    }

    public boolean isBound(){
        return isBound;
    }

    /**
     * If two constraint are compared, they compare their values.
     * It's possible to call this equals() method with an object of generic type {@link T}!
     * Then the value of this constraint gets compared to the given object.
     * @param obj Object of type Constraint or the generic type {@link T}
     * @return true if the values are equal or if the value of the constraint equals the {@link T} Object.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        else if(obj instanceof Constraint)
            return ((Constraint<?>) obj).value.equals(value);

        else
            return value.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return  "Constraint<" + value.getClass().getSimpleName() + ">, Value: " + value;
    }
}



