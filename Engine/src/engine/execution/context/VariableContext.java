package engine.execution.context;

import engine.variable.Variable;

public interface VariableContext {
    Long getVariableValue(Variable variable);
    void setVariableValue(Variable variable, long value);
}
