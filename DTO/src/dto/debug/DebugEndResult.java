package dto.debug;

import java.util.Map;

public record DebugEndResult(Long output, Map<String, Long> variableMap, Long cycles) {
    public Long getOutput() {
        return output;
    }

    public Map<String, Long> getVariableMap() {
        return variableMap;
    }

    public Long getCycles() {
        return cycles;
    }
}
