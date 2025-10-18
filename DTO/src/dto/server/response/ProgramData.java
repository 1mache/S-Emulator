package dto.server.response;

// comes back as a HTTP response
public record ProgramData(
        String name,
        String userString,
        boolean isMain,
        String uploadedBy,
        int instructionCount,
        int maxExpansionDegree,
        int runCount,
        long avgCreditCost) {}