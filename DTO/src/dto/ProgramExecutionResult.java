package dto;

import java.util.List;
import java.util.Map;

public record ProgramExecutionResult(
        String programName,
        Long outputValue,
        Map<String, Long> variableMap,
        List<Long> inputs,
        int expansionDegree,
        long cycles,
        boolean failed
) {
    public String getProgramName() {
        return programName;
    }

    public Long getOutputValue() {
        return outputValue;
    }

    public Map<String, Long> getVariableMap() {
        return variableMap;
    }

    public List<Long> getInputs() {
        return inputs;
    }

    public int getExpansionDegree() {
        return expansionDegree;
    }

    public long getCycles() {
        return cycles;
    }

    public boolean isEndedEarly() {return failed;}
}
