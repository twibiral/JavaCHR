package wibiral.tim.newjavachr.rules;

import wibiral.tim.newjavachr.constraints.Constraint;
import wibiral.tim.newjavachr.exceptions.AlreadyDefinedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Type of rule that removes a part of the head after execution (head1 stays, head2 gets removed).
 * Original syntax:
 * {@code Head1 / Head2 <=> Guard | Body.}
 */
public class Simpagation extends Rule {
    private final int nrConstraintsHead1;
    private final int nrConstraintsHead2;

    // Default definitions for guard and head.
    private SimpagationGuard guard = (x1, x2) -> true;    // Guard that accepts evey constraints from head 1.
    private SimpagationBody body = (x1, x2, y) -> { };   // Empty body for the rule.

    private boolean guardIsSet = false;
    private boolean bodyIsSet = false;

    public Simpagation(int nrConstraintsHead1, int nrConstraintsHead2) {
        super(nrConstraintsHead1 + nrConstraintsHead2);
        this.nrConstraintsHead1 = nrConstraintsHead1;
        this.nrConstraintsHead2 = nrConstraintsHead2;
    }

    public Simpagation(String name, int nrConstraintsHead1, int nrConstraintsHead2) {
        super(name, nrConstraintsHead1 + nrConstraintsHead2);
        this.nrConstraintsHead1 = nrConstraintsHead1;
        this.nrConstraintsHead2 = nrConstraintsHead2;
    }

    public Simpagation(int sizeHead1, Head... head){
        super(head);
        nrConstraintsHead1 = sizeHead1;
        nrConstraintsHead2 = head.length - sizeHead1;
    }

    // TODO: Implement missing constuctor with head types

    @Override
    public List<Constraint<?>> apply(List<Constraint<?>> constraints) {
        if(constraints.size() != headSize())
            return null;

        Constraint<?>[] constraintsHead1 = new Constraint<?>[nrConstraintsHead1];
        Constraint<?>[] constraintsHead2 = new Constraint<?>[nrConstraintsHead2];

        for (int i = 0; i < nrConstraintsHead1; i++) {
            constraintsHead1[i] = constraints.get(i);
        }

        for (int i = 0; i <  nrConstraintsHead2; i++) {
            constraintsHead2[i] = constraints.get(i + nrConstraintsHead1);
        }

        ArrayList<Constraint<?>> newConstraints = new ArrayList<>();
        body.execute(constraintsHead1, constraintsHead2, newConstraints);
        // Combine the constraints of the first head and the newly generated constraints:
        newConstraints.addAll(Arrays.asList(constraintsHead1));

        return newConstraints;
    }

    @Override
    public boolean accepts(List<Constraint<?>> constraints) {
        if(constraints.size() != headSize())
            return false;

        Constraint<?>[] constraintsHead1 = new Constraint<?>[nrConstraintsHead1];
        Constraint<?>[] constraintsHead2 = new Constraint<?>[nrConstraintsHead2];

        for (int i = 0; i < nrConstraintsHead1; i++) {
            constraintsHead1[i] = constraints.get(i);
        }
        for (int i = 0; i < nrConstraintsHead2; i++) {
            constraintsHead2[i] = constraints.get(i + nrConstraintsHead1);
        }

        return guard.check(constraintsHead1, constraintsHead2);
    }

    /**
     * Used to define the guard of the rule by defining a lambda function. The lambda gets two arrays with constraints
     * (the two heads of the rule) and should return true if the constraints are accepted.
     * Example for the lambda:
     * {@code head1, head2 -> head1[0] > head2[1]}
     * where head is an array with constraints.
     * @param guard lambda function defined according to {@link SimpagationGuard}.
     * @return This object. (Used to chain function calls.)
     */
    public Simpagation guard(SimpagationGuard guard){
        if(guardIsSet) throw new AlreadyDefinedException("Guard is already defined for this rule!");

        this.guard = guard;
        guardIsSet = true;

        return this;
    }

    /**
     * Used to define the body of the rule by defining a lambda function. The lambda gets two arrays with constraints
     * (the two heads of the rule) and an empty list were new arrays are added. The body uses the constraints to calculate
     * something and adds new constraints to the given list.
     * @param body lambda function defined according to {@link SimpagationBody}.
     * @return This object. (Used to chain function calls.)
     */
    public Simpagation body(SimpagationBody body){
        if(bodyIsSet) throw new AlreadyDefinedException("Body is already defined for this rule!");

        this.body = body;
        this.bodyIsSet = true;

        return this;
    }
}