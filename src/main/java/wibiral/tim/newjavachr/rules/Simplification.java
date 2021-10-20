package wibiral.tim.newjavachr.rules;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.exceptions.AlreadyDefinedException;

import java.util.ArrayList;
import java.util.List;

public class Simplification extends Rule {
    // Default definitions of body and guard
    private Guard guard = x -> true;    // Guard that accepts evey constraints for the rule.
    private Body body = (x, y) -> { };   // Empty body for the rule.

    private boolean guardIsSet = false;
    private boolean bodyIsSet = false;

    public Simplification(int nrConstraintsInHead) {
        super(nrConstraintsInHead);
    }

    public Simplification(int nrConstraintsInHead, String name) {
        super(nrConstraintsInHead, name);
    }

    @Override
    public boolean apply(List<Constraint<?>> constraints) {
        if(constraints.size() != headSize())
            return false;

        ArrayList<Constraint<?>> newConstraints = new ArrayList<>();
        body.execute(constraints.toArray(new Constraint<?>[0]), newConstraints);

        constraints.clear();                // Simplification removes all old constraints
        constraints.addAll(newConstraints); // and just adds new ones

        return true;
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