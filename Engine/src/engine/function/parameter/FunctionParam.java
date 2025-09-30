package engine.function.parameter;

import engine.execution.context.RunContext;

public interface FunctionParam {
    record EvaluationResult(long value, long calculationCyclesCost){}

    /**
     * @return what the parameter evaluates to when a function is called with it
     */
    EvaluationResult eval(RunContext context);

    String stringRepresentation();
}
