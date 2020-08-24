package wibiral.tim.javachr.rules;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.exceptions.AlreadyDefinedException;

import java.util.ArrayList;
import java.util.List;

public class Simplification extends Rule {
    private Guard guard = x -> true;    // Guard that accepts evey constraints for the rule.
    private Body body = (x, y) -> { };   // Empty body for the rule.

    private boolean guardIsSet = false;
    private boolean bodyIsSet = false;

    public Simplification(int nrConstraintsInHead) {
        super(nrConstraintsInHead);
    }

    @Override
    public boolean apply(ConstraintStore store) {
        if(store.size() != headSize())
            return false;

        Constraint<?>[] constraints = new Constraint[headSize()];
        for (int i = 0; i < headSize(); i++) {
            constraints[i] = store.get(i);
        }

        store.clear();

        ArrayList<Constraint<?>> newConstraints = new ArrayList<>();
        body.execute(constraints, newConstraints);
        store.addAll(newConstraints);

        return true;
    }

    @Override
    public boolean accepts(ConstraintStore constraints) {
        return constraints.size() == headSize() && guard.check(constraints.getAll().toArray(new Constraint[0]));
    }

    @Override
    public boolean accepts(List<Constraint<?>> constraints) {
        return constraints.size() == headSize() && guard.check(constraints.toArray(new Constraint[0]));
    }

    public Simplification guard(Guard guard){
        if(guardIsSet) throw new AlreadyDefinedException("Guard is already defined for this rule!");

        this.guard = guard;
        guardIsSet = true;

        return this;
    }

    public Simplification body(Body body){
        if(bodyIsSet) throw new AlreadyDefinedException("Body is already defined for this rule!");

        this.body = body;
        this.bodyIsSet = true;

        return this;
    }
}