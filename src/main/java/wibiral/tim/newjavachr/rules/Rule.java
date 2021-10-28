package wibiral.tim.newjavachr.rules;

import wibiral.tim.newjavachr.Constraint;

import java.util.List;

public abstract class Rule {
    protected final int nrConstraintsInHead;
    protected final String name;
    protected final boolean headTypesSpecified;
    protected final Class<?>[] headTypes;

    protected Rule(int nrConstraintsInHead){
        this.nrConstraintsInHead = nrConstraintsInHead;
        name = null;
        headTypesSpecified = false;
        this.headTypes = null;
    }

    protected Rule(String name, int nrConstraintsInHead){
        this.nrConstraintsInHead = nrConstraintsInHead;
        this.name = name;
        headTypesSpecified = false;
        this.headTypes = null;
    }

    protected Rule(String name, Class<?>... headTypes){
        this.nrConstraintsInHead = headTypes.length;
        this.name = name;
        headTypesSpecified = true;
        this.headTypes = headTypes;
    }

    // TODO: Add header + mechanics for unification and matching based on value and equality

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
     * Tests if the types of the constraints fit the types defined in the rule heads.
     * This method isn't very efficient and uses the isAssignableFrom method instead of instanceof.
     * @param constraints List of constraints to match with head types.
     * @return True if the constraints in the list match the head type, otherwise false.
     */
    protected boolean fitsHeadTypes(List<Constraint<?>> constraints) {
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
