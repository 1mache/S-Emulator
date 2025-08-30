package engine.api.dto;

import java.util.Map;

public record ExecutionResult(Long outputValue, Map<String, Long> variableMap, Long cyclesUsed) {
}
