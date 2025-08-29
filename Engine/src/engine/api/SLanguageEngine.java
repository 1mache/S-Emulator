package engine.api;

import engine.api.dto.ProgramPeek;
import engine.execution.ProgramRunner;
import engine.execution.exception.SEngineIllegalOperationException;
import engine.jaxb.loader.ProgramLoader;
import engine.jaxb.loader.XMLLoader;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.peeker.ProgramViewer;
import engine.program.Program;

import java.io.FileNotFoundException;

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

    public int getMaxExpansionDegree(){
        return programMaxDegree;
    }

    public ProgramPeek getProgramPeek() {
        return getExpandedProgramPeek(0) ;
    }

    public ProgramPeek getExpandedProgramPeek(int expansionDegree) {
        if(expansionDegree > programMaxDegree)
            throw new SEngineIllegalOperationException(
                    "The degree requested is bigger than the max degree:" + programMaxDegree
            );

        if(programNotLoaded())
            throw new SEngineIllegalOperationException("Program is not loaded");

        return new ProgramViewer(program).getProgramPeek(expansionDegree);
    }
}
