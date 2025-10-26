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
        long avgCreditCost,
        String architecture) {

    // JavaBean-style getters for JavaFX PropertyValueFactory
    public String getName() { return name; }
    public String getUploadedBy() { return uploadedBy; }
    public int getInstructionCount() { return instructionCount; }
    public int getMaxExpansionDegree() { return maxExpansionDegree; }
    public int getRunCount() { return runCount; }
    public long getAvgCreditCost() { return avgCreditCost; }
    public String getUserString() { return userString; }
    public boolean getIsMain() { return isMain; }
}