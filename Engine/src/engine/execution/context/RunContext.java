package engine.execution.context;

import engine.variable.Variable;

import java.util.Map;

public interface RunContext {
    Long getVariableValue(Variable variable);
    void setVariableValue(Variable variable, long value);
    Map<String, Long> getOrganizedVariableValues();
}
