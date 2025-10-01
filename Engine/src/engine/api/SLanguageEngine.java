package engine.api;

import engine.api.dto.debug.DebugHandle;
import engine.api.dto.ProgramExecutionResult;
import engine.api.dto.ProgramPeek;
import engine.debugger.ProgramDebugger;
import engine.execution.ProgramRunner;
import engine.execution.exception.SProgramNotLoadedException;
import engine.expansion.ProgramExpander;
import engine.function.Function;
import engine.loader.FromXMLProgramLoader;
import engine.loader.event.LoadingListener;
import engine.loader.exception.NotXMLException;
import engine.loader.exception.UnknownFunctionException;
import engine.loader.exception.UnknownLabelException;
import engine.peeker.ProgramViewer;
import engine.program.Program;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class SLanguageEngine {
    private String mainProgramName;
    private Program currentProgram;
    private final Map<String,Program> avaliablePrograms = new HashMap<>();
    private ProgramExpander programExpander;
    private List<ProgramExecutionResult> previousExecutions;

    private SLanguageEngine(){}
    // Singleton
    private static final SLanguageEngine instance = new SLanguageEngine();

    public static SLanguageEngine getInstance(){
        return instance;
    }

    public void loadProgram(String path, LoadingListener listener)
            throws NotXMLException, FileNotFoundException, UnknownLabelException, UnknownFunctionException {
        FromXMLProgramLoader loader = new FromXMLProgramLoader();
        loader.loadXML(path, listener);
        loader.validateProgram();
        currentProgram = loader.getProgram();

        mainProgramName = currentProgram.getName();
        avaliablePrograms.clear();
        avaliablePrograms.put(currentProgram.getName(), currentProgram);
        avaliablePrograms.putAll(
                loader.getFunctions().stream()
                        .collect(Collectors.toMap(
                                Program::getName, program -> program
                        ))
        );

        programExpander = new ProgramExpander(currentProgram);

        previousExecutions = new ArrayList<>();
    }

    public boolean programNotLoaded(){
        return currentProgram == null;
    }

    public int getMaxExpansionDegree() {
        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program has not been loaded");
        return currentProgram.getMaxExpansionDegree();
    }

    public ProgramPeek getProgramPeek() {
        return getExpandedProgramPeek(0) ;
    }

    public ProgramPeek getExpandedProgramPeek(int expansionDegree) {
        if(expansionDegree > getMaxExpansionDegree())
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + currentProgram.getMaxExpansionDegree());
        if(programNotLoaded()) {
            throw new SProgramNotLoadedException("Program is not loaded");
        }

        return new ProgramViewer(currentProgram).getProgramPeek(expansionDegree, programExpander);
    }

    public ProgramExecutionResult runProgram(List<Long> inputs, int expansionDegree, boolean specificInputs) {
        validateInputs(inputs);

        var expandedProgram = createExpandedProgram(expansionDegree);
        var runner = new ProgramRunner(expandedProgram);

        initializeInputs(runner, inputs, specificInputs);

        runner.run();

        var executionResult = new ProgramExecutionResult(
                runner.getRunOutput(),
                runner.getAllVariableValues(),
                inputs,
                expansionDegree,
                runner.getCycles()
        );
        previousExecutions.add(executionResult);
        return executionResult;
    }

    public DebugHandle debugProgram(List<Long> inputs, int expansionDegree, boolean specificInputs) {
       validateInputs(inputs);

        var expandedProgram = createExpandedProgram(expansionDegree);
        var debugger = new ProgramDebugger(expandedProgram);

        initializeInputs(debugger, inputs, specificInputs);

        return new DebugHandle(debugger);
    }


    // returns all the functions names that the program uses including the main program. the main is first in list
    public List<String> getAvaliableProgramsNames() {
        var functionNamesList = new ArrayList<>(avaliablePrograms.keySet().stream()
                .filter(functionName -> !functionName.equals(mainProgramName))
                .toList());
        // main program comes first, then functions
        functionNamesList.addFirst(mainProgramName);

        return functionNamesList;
    }

    public void setCurrentProgram(String programName) {
        Program requested = avaliablePrograms.get(programName);
        if(requested == null)
            throw new IllegalArgumentException("File does not contain program: " + programName);

        currentProgram = requested;
        programExpander = new ProgramExpander(currentProgram);
    }

    public List<ProgramExecutionResult> getExecutionHistory(){
        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program is not loaded");

        return previousExecutions;
    }

    // =============== private ===============

    private void validateInputs(List<Long> inputs) {
        for (var input : inputs) {
            if (input < 0)
                throw new IllegalArgumentException("Input values must be non-negative");
        }
        if (programNotLoaded())
            throw new SProgramNotLoadedException("Program is not loaded");
    }

    private Program createExpandedProgram(int expansionDegree) {
        if (expansionDegree > currentProgram.getMaxExpansionDegree())
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + currentProgram.getMaxExpansionDegree());
        return programExpander.expand(expansionDegree);
    }

    private void initializeInputs(ProgramRunner runner, List<Long> inputs, boolean specificInputs) {
        if (specificInputs)
            runner.initInputVariablesSpecific(inputs);
        else
            runner.initInputVariables(inputs);
    }
}
