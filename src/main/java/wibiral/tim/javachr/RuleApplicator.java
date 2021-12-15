package wibiral.tim.javachr;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.constraints.ConstraintStore;
import wibiral.tim.javachr.tracing.Tracer;

import java.util.List;

/**
 * RuleApplicators apply a set of rules to some given constraints.
 * Classes that implement this interface provides different "execute" methods which return a list with constraints after the rules were applied.
 */
public interface RuleApplicator {
    /**
     * Applies the defined rules to the given constraints.
     * @param constraints the constraints you want the rules applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    List<Constraint<?>> execute(List<Constraint<?>> constraints);

    /**
     * Applies the defined rules to the given constraints.
     * @param constraints the constraints you want the rules applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    List<Constraint<?>> execute(Constraint<?>... constraints);

    /**
     * Applies the defined rules to the given objects.
     * @param values Some objects you want the rules be applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    @SuppressWarnings("unchecked")
    <T> List<Constraint<?>> execute(T... values);

    /**
     * Applies the defined rules to the given objects.
     * @param store {@link ConstraintStore} with objects you want the rules be applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    List<Constraint<?>> execute(ConstraintStore store);

    /**
     * Tracers show you step-by-step how the rules are executed. Tracing only takes place if a tracer is passed to the
     * rule applicator here.
     * @param tracer The {@link Tracer} you want to use. (e.g. {@link wibiral.tim.javachr.tracing.CommandLineTracer})
     */
    void setTracer(Tracer tracer);

}
