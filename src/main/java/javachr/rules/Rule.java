package javachr.rules;

import javachr.constraints.Constraint;
import javachr.exceptions.AlreadyDefinedException;
import javachr.rules.head.HEAD_DEFINITION_TYPE;
import javachr.rules.head.Head;
import javachr.rules.head.VAR;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Rule {
    private static final AtomicLong ID_COUNTER = new AtomicLong(1);

    /**
     * The rule uniquely identified by the id
     */
    protected final long ID;
    protected final int nrConstraintsInHead;
    protected final String name;
    protected final Class<?>[] headTypes;
    protected final Head[] headDefinitions;
    protected final HEAD_DEFINITION_TYPE headDefinitionType;
    protected final EnumMap<VAR, ArrayList<Integer>> variableBindings = new EnumMap<>(VAR.class);

    /**
     * Used for negation-as-absence like in production rules.
     * Constraints that fit the heads in this array are not allowed to be in the constraint store.
     *
     * Rules with negation-as-absence have to implement a method that sets this fields. The fields are defined here for
     * easier access in the rule applicator.
     */
    protected Head[] negated;
    protected boolean negatedHeadsDefined = false;


    protected Rule(int nrConstraintsInHead){
        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();

        this.headDefinitionType = HEAD_DEFINITION_TYPE.SIZE_SPECIFIED;
        this.nrConstraintsInHead = nrConstraintsInHead;
        this.name = this.getClass().getSimpleName() + "[ID=" + ID + "]";
        this.headTypes = null;
        this.headDefinitions = null;
    }

    protected Rule(String name, int nrConstraintsInHead){
        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();

        this.headDefinitionType = HEAD_DEFINITION_TYPE.SIZE_SPECIFIED;
        this.nrConstraintsInHead = nrConstraintsInHead;
        this.name = name;
        this.headTypes = null;
        this.headDefinitions = null;
    }

    protected Rule(Class<?>... headTypes){
        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();

        this.headDefinitionType = HEAD_DEFINITION_TYPE.TYPES_SPECIFIED;
        this.nrConstraintsInHead = headTypes.length;
        this.name = this.getClass().getSimpleName() + "[ID=" + ID + "]";
        this.headTypes = headTypes;
        this.headDefinitions = null;
    }

    protected Rule(String name, Class<?>... headTypes){
        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();

        this.headDefinitionType = HEAD_DEFINITION_TYPE.TYPES_SPECIFIED;
        this.nrConstraintsInHead = headTypes.length;
        this.name = name;
        this.headTypes = headTypes;
        this.headDefinitions = null;
    }

    protected Rule(Head... headDefinitions){
        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();

        this.headDefinitionType = HEAD_DEFINITION_TYPE.COMPLEX_DEFINITION;
        this.headDefinitions = headDefinitions;
        this.nrConstraintsInHead = headDefinitions.length;
        this.name = this.getClass().getSimpleName() + "[ID=" + ID + "]";
        this.headTypes = null;

        for (int i = 0; i < nrConstraintsInHead; i++) {
            Head headDef = headDefinitions[i];

            if(headDef.isBoundTo() != VAR.NONE){
                if (!variableBindings.containsKey(headDef.isBoundTo())){
                    variableBindings.put(headDef.isBoundTo(), new ArrayList<>());
                }

                variableBindings.get(headDef.isBoundTo()).add(i);
            }
        }
    }

    protected Rule(String name, Head... headDefinitions){
        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();

        this.headDefinitionType = HEAD_DEFINITION_TYPE.COMPLEX_DEFINITION;
        this.headDefinitions = headDefinitions;
        this.nrConstraintsInHead = headDefinitions.length;
        this.name = name;
        this.headTypes = null;

        for (int i = 0; i < nrConstraintsInHead; i++) {
            Head headDef = headDefinitions[i];

            if(headDef.isBoundTo() != null){
                if (!variableBindings.containsKey(headDef.isBoundTo())){
                    variableBindings.put(headDef.isBoundTo(), new ArrayList<>());
                }

                variableBindings.get(headDef.isBoundTo()).add(i);
            }
        }
    }

    /**
     * @return Number of constraints in the head.
     */
    public int headSize(){
        return nrConstraintsInHead;
    }

    /**
     * @return Name of the rule.
     */
    public String name(){
        return name;
    }

    /**
     * Takes the constraints of the list and tests if the guard of the rule accepts them. (By using the defined guard.)
     * @param constraints List of constraints that are tested by the guard.
     * @return True if the guard of the Rule accepts the given {@link Constraint}s.
     */
    public abstract boolean accepts(List<Constraint<?>> constraints);

    /**
     * Applies the rule to the list of constraints. (By using the defined body)
     * The result of the rule application is saved in the list that was given as parameter! Maybe some constraints get
     * removed from the list and maybe some others are added.
     * If there are wrong constraints in the ConstraintStore the method maybe throws an exception but maybe just executes the wrong way.
     * DOES NOT CHECK IF THE GIVEN CONSTRAINTS ARE ACCEPTED TO IMPROVE PERFORMANCE!
     *
     * @param constraints A list of constraint on which the rule is applied or {@code null} if goes wrong.
     * @return true if the rule was successfully applied to the constraints of the list.
     */
    public abstract List<Constraint<?>> apply(List<Constraint<?>> constraints);

    /**
     * Returns the distinct ID of this rule.
     */
    public long ID(){
        return ID;
    }

    /**
     * The propagation history of a rule is saved if this method returns true.
     * (True for the {@link Propagation} rule.)
     */
    public boolean saveHistory(){
        return false;
    }

    /**
     * @return Enum element that specifies how the head of this rule was definied.
     */
    public HEAD_DEFINITION_TYPE getHeadDefinitionType(){
        return this.headDefinitionType;
    }

    /**
     * @return An array that contains the classes to which the constraints in the header must match.
     */
    public Class<?>[] getHeadTypes(){
        return headTypes;
    }

    /**
     * @return An Array that contains a head object for every Constraint in the header. The head object contains infos about the definition of the head constraint.
     */
    public Head[] getHeadDefinitions(){
        return headDefinitions;
    }

    /**
     * When the rule is defined with a complex header definition it is possible to bind head constraints to variable.
     * Head constraints bound to the same variable are then matched to constraints with equal internal object.
     * @return An enum map that contains lists of integers, which are indices. The indices of a list tell the position
     * of constraints in the header that must be equal.
     */
    public EnumMap<VAR, ArrayList<Integer>> getVariableBindings(){
        return variableBindings;
    }

    /**
     * Tests if the types of the constraints fit the types defined in the rule heads.
     * This method isn't very efficient and uses the isAssignableFrom method instead of instanceof.
     * @param constraints List of constraints to match with head types.
     * @return True if the constraints in the list match the head type, otherwise false.
     */
    public boolean fitsHeadTypes(List<Constraint<?>> constraints) {
        try{
            for (int i = 0; i < this.headSize(); i++) {
                if(!( headTypes[i].isAssignableFrom(constraints.get(i).getClass()) ))
                    return false;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean hasNegatedHeadConstraints() {
        return this.negatedHeadsDefined;
    }

    public Head[] getNegatedHeadConstraints() {
        return this.negated;
    }

    @Override
    public String toString() {
        return name;
    }
}
