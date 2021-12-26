package javachr.rules;

import javachr.constraints.Constraint;
import javachr.exceptions.AlreadyDefinedException;
import javachr.rules.body.Body;
import javachr.rules.body.BodyStore;
import javachr.rules.guard.Guard;
import javachr.rules.head.Head;

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

    public Simplification(Class<?>... headTypes){super(headTypes);}

    public Simplification(String name, Class<?>... headTypes){super(name, headTypes);}

    public Simplification(Head... headDefinitions){
        super(headDefinitions);
    }

    public Simplification(String name, Head... headDefinitions){
        super(name, headDefinitions);
    }

    @Override
    public List<Constraint<?>> apply(Constraint<?>[] constraints) {
        if(constraints.length != headSize())
            return null;

        BodyStore newConstraints = new BodyStore();
        body.execute(constraints, newConstraints);

        return newConstraints.getAll();                 // return the new constraints
    }

    @Override
    public boolean accepts(Constraint<?>[] constraints) {
        return constraints.length == headSize() && guard.check(constraints);
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


    /**
     * Used for negation-as-absence like in production rules.
     *
     * All the given constraint definitions are negated. That means the rule applicator makes sure that there are no
     * constraints like the defines ones in the constraint store.
     *
     * Attention: Negations ignore all variable bindings! Head of type ANY are ignored!
     *
     * @param negatedHead Negated constraints (Constraints that are not allowed to be in the constraint store)
     * @return This rule. Use this to chain method calls.
     */
    public Simplification not(Head... negatedHead){
        if (this.negatedHeadsDefined)
            throw new AlreadyDefinedException("Negated head constraints already defined!");

        if (negatedHead.length > 0) {
            this.negatedHeadsDefined = true;
            this.negated = negatedHead;
        }

        return this;
    }
}