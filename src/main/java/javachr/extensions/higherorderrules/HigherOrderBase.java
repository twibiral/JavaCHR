package javachr.extensions.higherorderrules;

import javachr.rules.Rule;
import javachr.rules.head.HEAD_DEFINITION_TYPE;
import javachr.rules.head.Head;
import javachr.rules.head.VAR;

import java.util.ArrayList;
import java.util.EnumMap;

/**
 * This class is the base class for all higher order rules.
 * Takes a rule and stores it. Subclasses can then use this rule.
 * It overrides some methods of the Rule class to pass through the method calls to the rule that is wrapped.
 *
 * Example: {@link Twice} takes the stored rule and applies it twice to the given constraints.
 */
public abstract class HigherOrderBase extends Rule {
    protected final Rule rule;

    protected HigherOrderBase(Rule rule, String prefix) {
        super(prefix + rule.name(), rule.headSize());
        this.rule = rule;
    }

    @Override
    public HEAD_DEFINITION_TYPE getHeadDefinitionType(){
        return rule.getHeadDefinitionType();
    }

    @Override
    public Class<?>[] getHeadTypes(){
        return rule.getHeadTypes();
    }

    @Override
    public Head[] getHeadDefinitions(){
        return rule.getHeadDefinitions();
    }

    @Override
    public EnumMap<VAR, ArrayList<Integer>> getVariableBindings(){
        return rule.getVariableBindings();
    }

    @Override
    public boolean saveHistory() {
        return this.rule.saveHistory();
    }
}
