package dto.server.response;

import java.util.Map;

public record DebugStateInfo(boolean finished,
                             boolean noCredits,
                             Map<String,Long> variableMap,
                             Long cycles,
                             Integer stoppedOnLine) {
    public boolean getFinished() {
        return finished;
    }

    public Map<String, Long> getVariableMap() {
        return variableMap;
    }

    public Long getCycles() {
        return cycles;
    }

    public Integer getStoppedOnLine() {
        return stoppedOnLine;
    }
}
