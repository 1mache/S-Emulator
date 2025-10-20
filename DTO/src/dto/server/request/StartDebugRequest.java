package dto.server.request;

import java.util.List;

public record StartDebugRequest(
        String programName,
        int expansionDegree,
        List<Long> inputs,
        List<Integer> breakpoints
) {}
