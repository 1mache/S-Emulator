package engine.api.dto.debug;

import java.util.Map;

public record DebugEndResult(Long output, Map<String, Long> variableMap, Long cycles) {
}
