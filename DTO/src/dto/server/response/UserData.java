package dto.server.response;

public record UserData(
        String username,
        long totalCredits,
        long usedCredits,
        int programsUploaded,
        int functionsUploaded,
        int runCount) {
    public String getUsername()         { return username; }
    public long   getTotalCredits()     { return totalCredits; }
    public long   getUsedCredits()      { return usedCredits; }
    public int    getProgramsUploaded() { return programsUploaded; }
    public int    getFunctionsUploaded(){ return functionsUploaded; }
    public int    getRunCount()         { return runCount; }
}
