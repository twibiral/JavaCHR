package javachr.extensions;

import javachr.SimpleRuleApplicator;

/**
 * This class implements CHR^RP semantics as a rule applicator.
 * That means that it will apply rules in the order of their priority. The priority
 * of a rule is defined by the order of the parameters given to the constructor.
 *
 * In: {@code RuleApplicator ra = new RulePrioritizingApplicator(r1, r2, r3);}
 * the rules will be applied in the order of r1, r2, r3. The first rule that is given to
 * the constructor will be the rule with the highest priority.
 *
 * Note: This class simple delegates to {@link SimpleRuleApplicator} because
 * {@link SimpleRuleApplicator} already implements the required semantics implicitly.
 */
public class RulePrioritizingApplicator extends SimpleRuleApplicator {
    // CHR^RP semantics already implemented implicitly in SimpleRuleApplicator.
}
