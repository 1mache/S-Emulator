package engine.execution.context;

import engine.variable.Variable;

import java.util.HashMap;
import java.util.Map;

public class VariableTable implements VariableContext {
    Map<String, Long> variableMap = new HashMap<>();

    @Override
    public Long getVariableValue(Variable variable) {
        return variableMap.getOrDefault(variable.stringRepresentation(), 0L);
    }

    @Override
    public void setVariableValue(Variable variable, long value) {
        variableMap.put(variable.stringRepresentation(), value);
    }
}
