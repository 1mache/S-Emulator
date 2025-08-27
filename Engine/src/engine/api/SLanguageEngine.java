package engine.api;

import engine.api.dto.InstructionPeek;
import engine.api.dto.ProgramPeek;
import engine.execution.ProgramRunner;
import engine.execution.exception.SEngineIllegalOperationException;
import engine.instruction.Instruction;
import engine.jaxb.loader.ProgramLoader;
import engine.jaxb.loader.XMLLoader;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.label.Label;
import engine.peeker.ProgramPeeker;
import engine.program.Program;
import engine.variable.Variable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public ProgramPeek getProgramPeek() {
        return getExpandedProgramPeek(1) ;
    }

    public ProgramPeek getExpandedProgramPeek(int expansionDegree) {
        if(expansionDegree > programMaxDegree)
            throw new SEngineIllegalOperationException(
                    "The degree requested is bigger than the max degree:" + programMaxDegree
            );

        if(programNotLoaded())
            throw new SEngineIllegalOperationException("Program is not loaded");

        return new ProgramPeeker(program).getProgramPeek(expansionDegree);
    }
}
