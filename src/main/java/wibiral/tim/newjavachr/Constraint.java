package wibiral.tim.newjavachr;

import java.util.concurrent.atomic.AtomicLong;

public class Constraint<T> {
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);

    private T value;
    private long id;

    public Constraint(T value){
        this.value = value;

        // gives this constraint a new id and increments the id counter for the next constraint.
        id = ID_COUNTER.getAndIncrement();
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
     * Instead of deleting an old constraint and creating a new one this method allows you to update an existing constraint.
     * This way is more efficient.
     * @param newValue The new value that is assigned to the constraint.
     */
    public void update(T newValue){
        value = newValue;
        id = ID_COUNTER.getAndIncrement();
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
    public String toString() {
        return  "Constraint<" + value.getClass().getSimpleName() + ">: " + value;
    }
}



