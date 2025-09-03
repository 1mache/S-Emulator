package engine.execution.context;

import engine.variable.Variable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class VariableTable implements VariableContext {
    Map<Variable, Long> variableMap = new HashMap<>();

    @Override
    public Long getVariableValue(Variable variable) {
        if(variable.equals(Variable.createWorkVariable(9)))
            System.out.println("Here");
        return variableMap.getOrDefault(variable, 0L);
    }

    @Override
    public void setVariableValue(Variable variable, long value) {
        variableMap.put(variable, value);
    }

    @Override
    public Map<String, Long> getOrganizedVariableValues() {
        return variableMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Variable.VARIABLE_COMPARATOR))
                .collect(Collectors.toMap(
                        e -> e.getKey().stringRepresentation(),
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
