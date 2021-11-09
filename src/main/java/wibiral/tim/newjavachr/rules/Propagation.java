package wibiral.tim.newjavachr.rules;

import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.exceptions.AlreadyDefinedException;

import java.util.ArrayList;
import java.util.List;

/**
 * The type of rule that just adds new constraints and removes none.
 * Original syntax:
 * {@code Head => Guard | Body.}
 */
public class Propagation extends Rule {
    // Default definitions of body and guard
    private Guard guard = x -> true;    // Guard that accepts evey constraints for the rule.
    private Body body = (x, y) -> { };   // Empty body for the rule.

    private boolean guardIsSet = false;
    private boolean bodyIsSet = false;

    public Propagation(int nrOfConstraints) {
        super(nrOfConstraints);
    }

    public Propagation(String name, int nrOfConstraints) {
        super(name, nrOfConstraints);
    }

    public Propagation(String name, Class<?>... headTypes){super(name, headTypes);}

    public Propagation(Head... headDefinitions){
        super(headDefinitions);
    }

    @Override
    public List<Constraint<?>> apply(List<Constraint<?>> constraints) {
        if(constraints.size() != headSize())
            return null;

        ArrayList<Constraint<?>> newConstraints = new ArrayList<>(); // All new constraints get added to this list by the body
        body.execute(constraints.toArray(new Constraint<?>[0]), newConstraints);

        // new constraints are added to the constraints list to add them to the constraint store after rule execution.
        constraints.addAll(newConstraints);

        return constraints;
    }

    @Override
    public boolean accepts(List<Constraint<?>> constraints) {
        if(constraints.size() != headSize())
            return false;

        // Assume that the solver checks this:
//        if(head_definition_type == HEAD_DEFINITION_TYPE.TYPES_SPECIFIED && !fitsHeadTypes(constraints))
//            return false;

        return guard.check(constraints.toArray(new Constraint<?>[0]));
    }

    /**
     * Used to define the guard of the rule by defining a lambda function. The lambda gets an array with constraints
     * (the head of the rule) and should return true if the constraints are accepted.
     * Example for the lambda:
     * {@code head -> head[0] > head[1]}
     * where head is an array with constraints.
     * @param guard lambda function defined according to {@link Guard}.
     * @return This object. (Used to chain function calls.)
     */
    public Propagation guard(Guard guard){
        if(guardIsSet) throw new AlreadyDefinedException("Guard is already defined for this rule!");

        this.guard = guard;
        guardIsSet = true;

        return this;
    }

    /**
     * Used to define the body of the rule by defining a lambda function. The lambda gets an array with constraints
     * (the head of the rule) and an empty list were new arrays are added. The body uses the constraints to calculate
     * something and adds new constraints to the given list.
     * @param body lambda function defined according to {@link Body}.
     * @return This object. (Used to chain function calls.)
     */
    public Propagation body(Body body){
        if(bodyIsSet) throw new AlreadyDefinedException("Body is already defined for this rule!");

        this.body = body;
        this.bodyIsSet = true;

        return this;
    }

    /**
     * The propagation history of a rule is saved if this method returns true.
     */
    @Override
    public boolean saveHistory(){
        return true;
    }
}