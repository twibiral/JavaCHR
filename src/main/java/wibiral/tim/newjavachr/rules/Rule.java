package wibiral.tim.newjavachr.rules;

import wibiral.tim.newjavachr.constraints.Constraint;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Rule {
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);

    /**
     * The rule uniquely identified by the id
     */
    protected final long ID;
    protected final int nrConstraintsInHead;
    protected final String name;
    protected final Class<?>[] headTypes;
    protected final Head[] headDefinitions;
    protected final HEAD_DEFINITION_TYPE headDefinitionType;

    protected Rule(int nrConstraintsInHead){
        this.headDefinitionType = HEAD_DEFINITION_TYPE.SIZE_SPECIFIED;
        this.nrConstraintsInHead = nrConstraintsInHead;
        this.name = null;
        this.headTypes = null;
        this.headDefinitions = null;

        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();
    }

    protected Rule(String name, int nrConstraintsInHead){
        this.headDefinitionType = HEAD_DEFINITION_TYPE.SIZE_SPECIFIED;
        this.nrConstraintsInHead = nrConstraintsInHead;
        this.name = name;
        this.headTypes = null;
        this.headDefinitions = null;

        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();
    }

    protected Rule(Class<?>... headTypes){
        this.headDefinitionType = HEAD_DEFINITION_TYPE.TYPES_SPECIFIED;
        this.nrConstraintsInHead = headTypes.length;
        this.name = null;
        this.headTypes = headTypes;
        this.headDefinitions = null;

        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();
    }

    protected Rule(String name, Class<?>... headTypes){
        this.headDefinitionType = HEAD_DEFINITION_TYPE.TYPES_SPECIFIED;
        this.nrConstraintsInHead = headTypes.length;
        this.name = name;
        this.headTypes = headTypes;
        this.headDefinitions = null;

        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();
    }

    protected Rule(Head... headDefinitions){
        this.headDefinitionType = HEAD_DEFINITION_TYPE.COMPLEX_DEFINITION;
        this.headDefinitions = headDefinitions;
        this.nrConstraintsInHead = headDefinitions.length;
        this.name = null;
        this.headTypes = null;

        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();
    }

    protected Rule(String name, Head... headDefinitions){
        this.headDefinitionType = HEAD_DEFINITION_TYPE.COMPLEX_DEFINITION;
        this.headDefinitions = headDefinitions;
        this.nrConstraintsInHead = headDefinitions.length;
        this.name = name;
        this.headTypes = null;

        // give this rule an ID
        ID = ID_COUNTER.getAndIncrement();
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
     * Takes the constraints of the list and tests if the guard of the rule accepts them. (By using the defined guard.)
     * @param constraints List of constraints that are tested by the guard.
     * @return True if the guard of the Rule accepts the given {@link Constraint}s.
     */
    public abstract boolean accepts(List<Constraint<?>> constraints);

    /**
     * Returns the distinct ID of this rule.
     */
    public long ID(){
        return ID;
    }

    /**
     * The propagation history of a rule is saved if this this method returns true.
     * (True for the {@link Propagation} rule.
     */
    public boolean saveHistory(){
        return false;
    }

    /**
     * TODO: Remove when removing is safe
     * @return true if types for the head constraints were defined in teh constructor.
     */
    public boolean headTypesSpecified(){
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
}
