package dto.server.response;

// comes back as a HTTP response
public record FunctionData(
        String name,
        String userString,
        boolean isProgram,
        String uploadedBy,
        int instructionCount,
        int maxExpansionDegree,
        int runCount,
        long avgCreditCost) {}