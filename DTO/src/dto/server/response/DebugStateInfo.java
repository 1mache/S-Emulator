package dto.server.response;

import java.util.Map;

public record DebugStateInfo(boolean finished, Map<String,Long> variableMap, Long cycles, Integer stoppedOnLine) {}
