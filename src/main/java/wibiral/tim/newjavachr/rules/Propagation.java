package wibiral.tim.newjavachr.rules;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.exceptions.AlreadyDefinedException;

import java.util.ArrayList;
import java.util.List;

public class Propagation extends Rule {
    private Guard guard = x -> true;    // Guard that accepts evey constraints for the rule.
    private Body body = (x, y) -> { };   // Empty body for the rule.

    private boolean guardIsSet = false;
    private boolean bodyIsSet = false;

    public Propagation(int nrOfConstraints) {
        super(nrOfConstraints);
    }

    public Propagation(int nrOfConstraints, String name) {
        super(nrOfConstraints, name);
    }

    public Propagation(Class<?>... headTypes){super(headTypes);}

    @Override
    public boolean apply(List<Constraint<?>> constraints) {
        if(constraints.size() != headSize())
            return false;

        ArrayList<Constraint<?>> newConstraints = new ArrayList<>(); // All new constraints get added to this list by the body
        body.execute(constraints.toArray(new Constraint<?>[0]), newConstraints);

        // new constraints are added to the constraints list to add them to the constraint store after rule execution.
        constraints.addAll(newConstraints);

        return true;
    }

    @Override
    public boolean accepts(List<Constraint<?>> constraints) {
        if(constraints.size() != headSize())
            return false;

        if(headTypesSpecified && !fitsHeadTypes(constraints))
            return false;

        return guard.check(constraints.toArray(new Constraint[0]));
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