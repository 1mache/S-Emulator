package engine.execution.context;

import engine.variable.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class VariableTable implements VariableContext {
    Map<String, Long> variableMap = new HashMap<>();

    @Override
    public Long getVariableValue(Variable variable) {
        var variableValue = Optional.ofNullable(variableMap.get(variable.stringRepresentation()));
        AtomicReference<Long> res = new AtomicReference<>(0L);
        variableValue.ifPresent(res::set);
        return res.get();
    }

    @Override
    public void setVariableValue(Variable variable, long value) {
        variableMap.put(variable.stringRepresentation(), value);
    }
}
