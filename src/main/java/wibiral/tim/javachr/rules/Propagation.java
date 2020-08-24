package wibiral.tim.javachr.rules;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.exceptions.AlreadyDefinedException;
import wibiral.tim.javachr.exceptions.BodyUndefinedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Propagation extends Rule {
    private Guard guard = x -> true;    // Guard that accepts evey constraints for the rule.
    private Body body = (x, y) -> { };   // Empty body for the rule.

    private boolean guardIsSet = false;
    private boolean bodyIsSet = false;

    /**
     * This list is used by the Rule to check if the rule was already applied to a given set of constraints.
     * It compares the content of the constraints, not the constraint itself.
     * Attention: It's recommended to implement the .equals() method for the classes that are used in constraints.
     */
    private final List<List<Constraint<?>>> propagatedConstrains = new ArrayList<>();


    public Propagation(int nrOfConstraints) {
        super(nrOfConstraints);
    }

    @Override
    public boolean apply(ConstraintStore store) {
//        if(!bodyIsSet)
//            throw new BodyUndefinedException("To apply rules to constraints, the rule body must be defined!");

        if(store.size() != headSize())
            return false;

        Constraint<?>[] constraints = new Constraint[headSize()];
        for (int i = 0; i < store.size(); i++) {
            constraints[i] = store.get(i);
        }

        ArrayList<Constraint<?>> newConstraints = new ArrayList<>();
        body.execute(constraints, newConstraints);
        store.addAll(newConstraints);

        propagatedConstrains.add(Arrays.asList(constraints));

        return true;
    }

    @Override
    public boolean accepts(ConstraintStore constraints) {
        return constraints.size() == headSize() && guard.check(constraints.getAll().toArray(new Constraint[0]))
                && ! propagatedConstrains.contains(constraints.getAll());
    }

    @Override
    public boolean accepts(List<Constraint<?>> constraints) {
        return constraints.size() == headSize() && guard.check(constraints.toArray(new Constraint[0]))
                && ! propagatedConstrains.contains(constraints);
    }

    public Propagation guard(Guard guard){
        if(guardIsSet) throw new AlreadyDefinedException("Guard is already defined for this rule!");

        this.guard = guard;
        guardIsSet = true;

        return this;
    }

    public Propagation body(Body body){
        if(bodyIsSet) throw new AlreadyDefinedException("Body is already defined for this rule!");

        this.body = body;
        this.bodyIsSet = true;

        return this;
    }
}