package wibiral.tim.javachr;

import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.tracing.Tracer;

import java.util.List;

/**
 * Constraint solvers apply a set of rules to some given constraints.
 * Classes that implement this interface provide different "solve" methods which return a list with constraints after the rules were applied.
 */
public interface ConstraintSolver {
    /**
     * Applies the defined rules to the given constraints.
     * @param constraints the constraints you want the rules applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    List<Constraint<?>> solve(List<Constraint<?>> constraints);

    /**
     * Applies the defined rules to the given constraints.
     * @param constraints the constraints you want the rules applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    List<Constraint<?>> solve(Constraint<?>... constraints);

    /**
     * Applies the defined rules to the given objects.
     * @param values Some objects you want the rules be applied to.
     * @return The result after no more rules can be applied to the constraints.
     */
    <T> List<Constraint<?>> solve(T... values);

    /**
     * Adds a rule to the constraint handler.
     * @param rule The rule to add.
     */
    void addRule(Rule rule);

    /**
     * Tracers show you step-by-step how the rules are executed. Tracing only takes place if a tracer is passed to the
     * constraint solver here.
     * @param tracer The {@link Tracer} you want to use. (e.g. {@link wibiral.tim.javachr.tracing.CommandLineTracer})
     */
    void setTracer(Tracer tracer);

}
