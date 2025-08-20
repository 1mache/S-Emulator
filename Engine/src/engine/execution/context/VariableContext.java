package engine.execution.context;

import engine.variable.Variable;

import java.util.Optional;

public interface VariableContext {
    Long getVariableValue(Variable variable);
    void setVariableValue(Variable variable, long value);
}
