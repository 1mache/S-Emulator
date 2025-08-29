package console;

import engine.api.SLanguageEngine;
import engine.api.dto.ProgramPeek;
import engine.execution.exception.SEngineIllegalOperationException;

public class ExpandProgramOption extends AbstractExpandingOption {

    public ExpandProgramOption() {
        super("Expand Program", "");
    }

    @Override
    public void execute(SLanguageEngine engine) {
        if(engine.programNotLoaded()){
            System.out.println("Error: Program not loaded! Load program first.");
            return;
        }

        int expansionDegree = getExpansionDegree(engine.getMaxExpansionDegree());
        try{
            ProgramPeek programPeek = engine.getExpandedProgramPeek(expansionDegree);
            System.out.println("Program name: " + programPeek.name());
            System.out.println("Input variables used: " + programPeek.inputVariables());
            System.out.println("Labels used: " + programPeek.labelsUsed());
            printProgramPeek(programPeek, expansionDegree);
        } catch(SEngineIllegalOperationException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}
