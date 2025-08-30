package engine.api;

import engine.api.dto.ExecutionResult;
import engine.api.dto.ProgramPeek;
import engine.execution.ProgramRunner;
import engine.execution.exception.SProgramNotLoadedException;
import engine.jaxb.loader.ProgramLoader;
import engine.jaxb.loader.XMLLoader;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.peeker.ProgramViewer;
import engine.program.Program;

import java.io.FileNotFoundException;
import java.util.List;

public class SLanguageEngine {
    private Program program;
    private ProgramRunner programRunner;
    private int programMaxDegree;

    // Singleton
    private static final SLanguageEngine instance = new SLanguageEngine();

    public static SLanguageEngine getInstance(){
        return instance;
    }

    private SLanguageEngine(){}

    public void loadProgram(String path)
            throws NotXMLException, FileNotFoundException, UnknownLabelException {
        XMLLoader loader = new ProgramLoader();
        loader.loadXML(path);

        loader.validateProgram();
        program = loader.getProgram();
        programRunner = new ProgramRunner(program);
        programMaxDegree = programRunner.getMaxExpansionDegree();
    }

    public boolean programNotLoaded(){
        return program == null;
    }

    public int getMaxExpansionDegree() throws SProgramNotLoadedException {
        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program has not been loaded");
        return programMaxDegree;
    }

    public ProgramPeek getProgramPeek() throws SProgramNotLoadedException {
        return getExpandedProgramPeek(0) ;
    }

    public ProgramPeek getExpandedProgramPeek(int expansionDegree) throws SProgramNotLoadedException {
        if(expansionDegree > programMaxDegree)
            throw new IllegalArgumentException(
                    "The degree requested is bigger than the max degree:" + programMaxDegree
            );

        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program is not loaded");

        return new ProgramViewer(program).getProgramPeek(expansionDegree);
    }

    public ExecutionResult runProgram(List<Long> inputs, int expansionDegree) throws SProgramNotLoadedException {
        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program is not loaded");

        programRunner.reset(); // reset previous run artifacts
        programRunner.initInputVariables(inputs);
        programRunner.run(expansionDegree);

        return new ExecutionResult(
                programRunner.getRunOutput(),
                programRunner.getVariableValues(),
                programRunner.getCycles());
    }
}
