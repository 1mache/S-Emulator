package engine.api;

import dto.ProgramExecutionResult;

import java.util.*;

public class RunHistory {
    private final Map<String, List<ProgramExecutionResult>> name2Runs = new LinkedHashMap<>();
    private final List<ProgramExecutionResult> runsInOrder = new ArrayList<>();

    public synchronized void addExecution(String programName, ProgramExecutionResult result) {
        List<ProgramExecutionResult> executionResults = name2Runs.get(programName);
        if (executionResults == null) {
            // key does not exist, create a new list with the item
            executionResults = new ArrayList<>();
            executionResults.add(result);
            name2Runs.put(programName, executionResults);
        } else {
            // key exists, add the item to the existing list
            executionResults.add(result);
        }

        runsInOrder.add(result);
    }

    public synchronized List<ProgramExecutionResult> getExecutionsOf(String programName) {
        List<ProgramExecutionResult> results = name2Runs.get(programName);

        // if no history for this function, return empty list
        return Objects.requireNonNullElseGet(results, List::of);
    }

    public synchronized int runCount() {
        return runsInOrder.size();
    }

    public synchronized List<ProgramExecutionResult> getAllExecutions() {
        return List.copyOf(runsInOrder);
    }

    public synchronized int runCount(String programName) {
        List<ProgramExecutionResult> results = name2Runs.get(programName);
        return results == null ? 0 : results.size();
    }
}
