package wibiral.tim.newjavachr.tracing;

import wibiral.tim.newjavachr.Constraint;
import wibiral.tim.newjavachr.ConstraintSolver;
import wibiral.tim.newjavachr.ConstraintStore;
import wibiral.tim.newjavachr.rules.Rule;

/**
 * Base class for tracers that help debugging by tracing the execution order of the rule execution.
 * Derived class may implement tracing for command line or with a graphical interface.
 */
public interface Tracer {
    /**
     * The {@link ConstraintSolver} tells the Tracer the last applied rule, the constraints to which the rule was applied and
     * the new constraints generated by the rule.
     * @param appliedRule The Rule that was applied.
     * @param oldConstraints Constraints to which the rule was applied.
     * @param addedConstraints New Constraints generated by the rule.
     * @return False if execution should be stopped, true if Handler should execute next step.
     */
    boolean step(Rule appliedRule, Constraint<?>[] oldConstraints, Constraint<?>[] addedConstraints);

    /**
     * The {@link ConstraintSolver} tells the tracer that he started operating and the tracer shows a corresponding message.
     * @param store the {@link ConstraintStore} the solver holds.
     */
    void initMessage(ConstraintStore store);

    /**
     * The {@link ConstraintStore} tells the tracer that it is stopping now and the tracer shows a corresponding message.
     * @param store the {@link ConstraintStore} the solver holds.
     */
    void stopMessage(ConstraintStore store);

    /**
     * The {@link ConstraintStore} tells the tracer that it terminated and the tracer shows a corresponding message.
     * @param store the {@link ConstraintStore} the solver holds.
     */
    void terminatedMessage(ConstraintStore store);
}
