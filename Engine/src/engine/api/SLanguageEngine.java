package engine.api;

import engine.api.dto.ExecutionResult;
import engine.api.dto.ProgramPeek;
import engine.execution.ProgramRunner;
import engine.execution.exception.SProgramNotLoadedException;
import engine.loader.FromXMLProgramLoader;
import engine.loader.event.LoadingListener;
import engine.loader.exception.NotXMLException;
import engine.loader.exception.UnknownLabelException;
import engine.peeker.ProgramViewer;
import engine.program.Program;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SLanguageEngine {
    private Program program;
    private ProgramRunner programRunner;
    private List<ExecutionResult> previousExecutions;

    private SLanguageEngine(){}
    // Singleton
    private static final SLanguageEngine instance = new SLanguageEngine();

    public static SLanguageEngine getInstance(){
        return instance;
    }

    public void loadProgram(String path, LoadingListener listener)
            throws NotXMLException, FileNotFoundException, UnknownLabelException {
        FromXMLProgramLoader loader = new FromXMLProgramLoader();
        loader.loadXML(path, listener);
        loader.validateProgram();
        program = loader.getProgram();

        programRunner = new ProgramRunner(program);
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

        return new ProgramViewer(program).getProgramPeek(expansionDegree);
    }

    public ExecutionResult runProgram(List<Long> inputs, int expansionDegree) {
        if(expansionDegree > program.getMaxExpansionDegree())
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + program.getMaxExpansionDegree());
        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program is not loaded");

        programRunner.reset(); // reset previous run artifacts
        programRunner.initInputVariables(inputs);
        programRunner.run(expansionDegree);

        var executionResult = new ExecutionResult(
                programRunner.getRunOutput(),
                programRunner.getVariableValues(),
                inputs,
                expansionDegree,
                programRunner.getCycles()
        );
        previousExecutions.add(executionResult);
        return executionResult;
    }

    public List<ExecutionResult> getExecutionHistory(){
        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program is not loaded");

        return previousExecutions;
    }
}
