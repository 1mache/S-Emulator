package engine.api;

import engine.api.dto.ExecutionResult;
import engine.api.dto.ProgramPeek;
import engine.execution.ProgramRunner;
import engine.execution.exception.SProgramNotLoadedException;
import engine.expansion.ProgramExpander;
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

    public ExecutionResult runProgram(List<Long> inputs, int expansionDegree, boolean specificInputs) {
        if(expansionDegree > program.getMaxExpansionDegree())
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + program.getMaxExpansionDegree());
        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program is not loaded");
        for(var input : inputs){
            if(input < 0)
                throw new IllegalArgumentException("Input values must be non-negative");
        }

        var expandedProgram = new ProgramExpander(program).expand(expansionDegree);
        var programRunner = new ProgramRunner(expandedProgram);

        if(specificInputs)
            programRunner.initInputVariablesSpecific(inputs);
        else
            programRunner.initInputVariables(inputs);

        programRunner.run();

        var executionResult = new ExecutionResult(
                programRunner.getRunOutput(),
                programRunner.getVariableEndValues(),
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
