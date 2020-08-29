package wibiral.tim.javachr.constraints;

public class Constraint<T> {
    private final T value;

    public Constraint(T value){
        this.value = value;
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



