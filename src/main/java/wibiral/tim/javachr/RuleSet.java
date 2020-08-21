package wibiral.tim.javachr;

import wibiral.tim.javachr.exceptions.RuleSetIsBlockedException;
import wibiral.tim.javachr.rules.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Holds a set of rules.
 * Rules can be added with the add(Rule rule) method and as constructor parameter.
 */
public class RuleSet {
    private final ArrayList<Rule> rules = new ArrayList<>();

    private boolean blocked = false;
    private int counter = 0;

    public RuleSet() {    }

    public RuleSet(List<Rule> rules){
        if(rules != null)
            this.rules.addAll(rules);
    }

    public RuleSet(Rule[] rules){
        if(rules.length > 0)
            this.rules.addAll(Arrays.asList(rules));
    }

    /**
     * Returns the next rule in the RuleSet. Begins with the first rule again after the last rule.
     * @return The next rule.
     */
    public Rule next(){
        Rule nextRule = rules.get(counter);
        counter = (counter + 1) % rules.size();
        return nextRule;
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

    void block(){
        blocked = true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Rule set:");
        for (Rule r : rules){
            str.append("\n\t").append(r.toString());
        }

        return str.toString();
    }
}


