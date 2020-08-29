package wibiral.tim.javachr;

import wibiral.tim.javachr.exceptions.RuleSetIsBlockedException;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simplification;

import java.lang.invoke.StringConcatFactory;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds a set of rules.
 * Rules can be added with the add(Rule rule) method and as constructor parameter.
 */
public class RuleSet {
    private final List<Rule> rules = new LinkedList<>();
    private boolean blocked = false;

    public RuleSet() {  }

    public RuleSet(Rule[] rules){
        if(rules.length > 0)
            this.rules.addAll(Arrays.asList(rules));
    }

    public Rule get(int index){
        return rules.get(index);
    }

    /**
     * @return true if no rules are in the store, otherwise false.
     */
    public boolean isEmpty(){
        return rules.isEmpty();
    }

    /**
     * Adds the rule to the end of the rules.
     * (Rules are applied one after another).
     * @param rule The rule to add.
     * @return true if added successful, otherwise false.
     */
    public boolean add(Rule rule){
        if(blocked) throw new RuleSetIsBlockedException("Can't add new Rules after instantiating a solver!");
        return rules.add(rule);
    }

    /**
     * @return Number of rules in this rule set.
     */
    public int size(){
        return rules.size();
    }

    /**
     * Package private that it can only be used ConstraintHandlers
     */
    void block(){
        blocked = true;
    }

    @Override
    public String toString() {
        int nrOfSimplifications = 0;
        int nrOfPropagations = 0;
        int nrOfSimpagations = 0;

        for(Rule rule : rules){
            if(rule instanceof Simplification)
                nrOfSimplifications++;
            else if(rule instanceof Propagation)
                nrOfPropagations++;
            else
                nrOfSimpagations++;
        }
         return String.format(
                 "Simplifications: %d%n" +
                 "Propagations: %d%n" +
                 "Simpagations: %d%n"
                 , nrOfSimplifications, nrOfPropagations, nrOfSimpagations
         );
    }
}


