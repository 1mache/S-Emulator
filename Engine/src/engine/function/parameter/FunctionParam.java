package engine.function.parameter;

import engine.execution.context.RunContext;

public interface FunctionParam {
    /**
     * @return what the parameter evaluates to when a function is called with it
     */
    Long eval(RunContext context);

    String stringRepresentation();
}
