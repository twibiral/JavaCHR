package javachr.extensions.higherorderrules;

import javachr.RuleApplicator;
import javachr.SimpleRuleApplicator;
import javachr.constraints.Constraint;
import javachr.rules.Propagation;
import javachr.rules.Rule;
import javachr.rules.Simplification;
import javachr.rules.head.Head;
import junit.framework.TestCase;

import java.util.List;

public class TwiceTest extends TestCase {

    public void testApply() {
        Rule p = new Propagation(Head.ofType(Integer.class))
                .body(((head, newConstraints) -> newConstraints.add(new Constraint<>("xxx"))));

        Rule s = new Simplification(Head.ofType(Integer.class))
                .body(((head, newConstraints) -> newConstraints.add(new Constraint<>("xxx"))));

        // ----- Propagation -----
        RuleApplicator applicator = new SimpleRuleApplicator(new Twice(p));
        List<Constraint<?>> result = applicator.execute(42);

        assertEquals(3, result.size());
        assertEquals(2, result.stream().filter(x -> x.innerObjEquals("xxx")).count());
        assertTrue(result.stream().anyMatch(x -> x.innerObjEquals(42)));

        // ----- Simplification -----
        applicator = new SimpleRuleApplicator(new Twice(s));
        result = applicator.execute(42);

        assertEquals(2, result.size());
        assertEquals(2, result.stream().filter(x -> x.innerObjEquals("xxx")).count());
        assertFalse(result.stream().anyMatch(x -> x.innerObjEquals(42)));
    }
}