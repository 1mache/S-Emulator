package engine.api.dto;

import java.util.List;
import java.util.Map;

public record ExecutionResult(
        Long outputValue,
        Map<String, Long> variableMap,
        List<Long> inputs,
        int expansionDegree,
        Long cyclesUsed
) {
}
