package engine.api.dto.debug;

import java.util.Map;

public record DebugEndResult(Map<String, Long> variableMap, Long cycles) {
}
