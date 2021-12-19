package javachr.rules.body;

import javachr.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * This simple class is used as body parameter to add new constraints to the constraint store.
 *
 */
public class GuardStore {
    private final List<Constraint<?>> store = new ArrayList<>();

    /**
     * Adds the constraint to the internal data structure.
     * @param constraint Constraint to add.
     */
    public void add(Constraint<?> constraint) {
        store.add(constraint);
    }

    /**
     * Creates new constraint with the value and adds it to the internal data structure.
     * @param value The value of the new constraint.
     * @param <T> The type of the value.
     */
    public <T> void add(T value) {
        store.add(new Constraint<>(value));
    }

    /**
     * @return The internal data structure.
     */
    public List<Constraint<?>> getAll() {
        return store;
    }

    /**
     * Delete all constraints from the internal data structure.
     */
    public void clear() {
        store.clear();
    }
}
