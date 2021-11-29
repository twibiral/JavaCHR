package wibiral.tim.javachr.extensions;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.exceptions.AlreadyDefinedException;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simplification;
import wibiral.tim.javachr.rules.body.Body;
import wibiral.tim.javachr.rules.guard.Guard;
import wibiral.tim.javachr.rules.head.HEAD_DEFINITION_TYPE;
import wibiral.tim.javachr.rules.head.Head;
import wibiral.tim.javachr.rules.head.VAR;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ElasticSimplification extends Rule {
    private final int minimumConstraintsInHead;
    private final int maximumConstraintsInHead;

    private int headConstraintCounter;

    // Default definitions of body and guard
    private Guard guard = x -> true;    // Guard that accepts evey constraints for the rule.
    private Body body = (x, y) -> { };   // Empty body for the rule.

    private boolean guardIsSet = false;
    private boolean bodyIsSet = false;

    public ElasticSimplification(String name, int minimumConstraintsInHead, int maximumConstraintsInHead) {
        super(name, maximumConstraintsInHead);
        this.minimumConstraintsInHead = minimumConstraintsInHead;
        this.maximumConstraintsInHead = maximumConstraintsInHead;

        this.headConstraintCounter = maximumConstraintsInHead;
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
    public ElasticSimplification guard(Guard guard){
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
    public ElasticSimplification body(Body body){
        if(bodyIsSet) throw new AlreadyDefinedException("Body is already defined for this rule!");

        this.body = body;
        this.bodyIsSet = true;

        return this;
    }

    /**
     * @return True if all the constraints are equal.
     */
    private boolean allEqual(List<Constraint<?>> constraints){
        for(int i = 0; i < constraints.size() - 1; i++){
            if(!constraints.get(i).equals(constraints.get(i+1))){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean accepts(List<Constraint<?>> constraints) {
        return allEqual(constraints) && guard.check(new Constraint[]{constraints.get(0)});
    }

    @Override
    public List<Constraint<?>> apply(List<Constraint<?>> constraints) {
        ArrayList<Constraint<?>> newConstraints = new ArrayList<>();
        body.execute(new Constraint[]{constraints.get(0)}, newConstraints);

        constraints.clear();                    // Simplification removes all old constraints
        constraints.addAll(newConstraints);     // and just adds new ones
        return constraints;                     // return the new constraints
    }

    /**
     * @return Number of constraints in the head.
     */
    @Override
    public int headSize(){
        int oldCounter = headConstraintCounter;
        headConstraintCounter = Math.min(minimumConstraintsInHead, headConstraintCounter-1);

        return oldCounter;
    }

    /**
     * @return Enum element that specifies how the head of this rule was definied.
     */
    @Override
    public HEAD_DEFINITION_TYPE getHeadDefinitionType(){
        return this.headDefinitionType;
    }

    /**
     * @return An array that contains the classes to which the constraints in the header must match.
     */
    @Override
    public Class<?>[] getHeadTypes(){
        return headTypes;
    }

    /**
     * @return An Array that contains a head object for every Constraint in the header. The head object contains infos about the definition of the head constraint.
     */
    @Override
    public Head[] getHeadDefinitions(){
        return headDefinitions;
    }

    /**
     * When the rule is defined with a complex header definition it is possible to bind head constraints to variable.
     * Head constraints bound to the same variable are then matched to constraints with equal internal object.
     * @return An enum map that contains lists of integers, which are indices. The indices of a list tell the position
     * of constraints in the header that must be equal.
     */
    @Override
    public EnumMap<VAR, ArrayList<Integer>> getVariableBindings(){
        return variableBindings;
    }
}
