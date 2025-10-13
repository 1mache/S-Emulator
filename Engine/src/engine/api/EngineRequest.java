package engine.api;

public record EngineRequest(String userName, String programName, int expansionDegree) {
    public EngineRequest(String userName, String programName) {
        this(userName, programName, 0);
    }
}
