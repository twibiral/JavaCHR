package wibiral.tim.newjavachr.rules;

import wibiral.tim.newjavachr.Constraint;
import wibiral.tim.newjavachr.exceptions.AlreadyDefinedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Type of rule that removes all header constraints and maybe adds new constraints in the body. (It "simplifies")
 * Original syntax:
 * {@code Head <=> Guard | Body.}
 */
public class Simplification extends Rule {
    // Default definitions of body and guard
    private Guard guard = x -> true;    // Guard that accepts evey constraints for the rule.
    private Body body = (x, y) -> { };   // Empty body for the rule.

    private boolean guardIsSet = false;
    private boolean bodyIsSet = false;

    public Simplification(int nrConstraintsInHead) {
        super(nrConstraintsInHead);
    }

    public Simplification(String name, int nrConstraintsInHead) {
        super(name, nrConstraintsInHead);
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

    /**
     * Used to define the guard of the rule by defining a lambda function. The lambda gets an array with constraints
     * (the head of the rule) and should return true if the constraints are accepted.
     * Example for the lambda:
     * {@code head -> head[0] > head[1]}
     * where head is an array with constraints.
     * @param guard lambda function defined according to {@link Guard}.
     * @return This object. (Used to chain function calls.)
     */
    public Simplification guard(Guard guard){
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
    public Simplification body(Body body){
        if(bodyIsSet) throw new AlreadyDefinedException("Body is already defined for this rule!");

        this.body = body;
        this.bodyIsSet = true;

        return this;
    }
}