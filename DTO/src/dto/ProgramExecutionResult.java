package dto;

import java.util.List;
import java.util.Map;

public record ProgramExecutionResult(
        Long outputValue,
        Map<String, Long> variableMap,
        List<Long> inputs,
        int expansionDegree,
        long cycles
) {
}
