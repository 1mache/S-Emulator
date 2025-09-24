package engine.function.parameter;

import engine.execution.context.VariableContext;

public interface FunctionParam {
    /**
     * @return what the parameter evaluates to when a function is called with it
     */
    Long eval(VariableContext context);
}
