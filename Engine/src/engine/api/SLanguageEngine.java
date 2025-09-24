package engine.api;

import engine.api.dto.debug.DebugHandle;
import engine.api.dto.ExecutionResult;
import engine.api.dto.ProgramPeek;
import engine.debugger.ProgramDebugger;
import engine.execution.ProgramRunner;
import engine.execution.exception.SProgramNotLoadedException;
import engine.expansion.ProgramExpander;
import engine.loader.FromXMLProgramLoader;
import engine.loader.event.LoadingListener;
import engine.loader.exception.NotXMLException;
import engine.loader.exception.UnknownFunctionException;
import engine.loader.exception.UnknownLabelException;
import engine.peeker.ProgramViewer;
import engine.program.Program;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SLanguageEngine {
    private Program program;
    private ProgramExpander programExpander;
    private List<ExecutionResult> previousExecutions;

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
        program = loader.getProgram();
        programExpander = new ProgramExpander(program);

        previousExecutions = new ArrayList<>();
    }

    public boolean programNotLoaded(){
        return program == null;
    }

    public int getMaxExpansionDegree() {
        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program has not been loaded");
        return program.getMaxExpansionDegree();
    }

    public ProgramPeek getProgramPeek() {
        return getExpandedProgramPeek(0) ;
    }

    public ProgramPeek getExpandedProgramPeek(int expansionDegree) {
        if(expansionDegree > getMaxExpansionDegree())
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + program.getMaxExpansionDegree());
        if(programNotLoaded()) {
            throw new SProgramNotLoadedException("Program is not loaded");
        }

        return new ProgramViewer(program).getProgramPeek(expansionDegree, programExpander);
    }

    public ExecutionResult runProgram(List<Long> inputs, int expansionDegree, boolean specificInputs) {
        validateInputs(inputs);

        var expandedProgram = createExpandedProgram(expansionDegree);
        var runner = new ProgramRunner(expandedProgram);

        initializeInputs(runner, inputs, specificInputs);

        runner.run();

        var executionResult = new ExecutionResult(
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

    public List<ExecutionResult> getExecutionHistory(){
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
        if (expansionDegree > program.getMaxExpansionDegree())
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + program.getMaxExpansionDegree());
        return programExpander.expand(expansionDegree);
    }

    private void initializeInputs(ProgramRunner runner, List<Long> inputs, boolean specificInputs) {
        if (specificInputs)
            runner.initInputVariablesSpecific(inputs);
        else
            runner.initInputVariables(inputs);
    }
}
