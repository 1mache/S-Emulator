package console.menu.option;

import console.menu.option.helper.ProgramName;
import engine.api.EngineRequest;
import engine.api.SLanguageEngine;
import engine.api.dto.ProgramPeek;
import engine.execution.exception.SProgramNotLoadedException;

public class ExpandProgramOption extends AbstractExpandingOption {

    public ExpandProgramOption() {
        super("Expand Program", "");
    }

    @Override
    public void execute(SLanguageEngine engine, ProgramName programName) {
        try{
            int expansionDegree = getExpansionDegree(engine.getMaxExpansionDegree(programName.get()));
            ProgramPeek programPeek = engine.getProgramPeek(
                    new EngineRequest(USERNAME, programName.get(), expansionDegree)
            );
            System.out.println("Program name: " + programPeek.name());
            System.out.println("Input variables used: " + programPeek.inputVariables());
            System.out.println("Labels used: " + programPeek.labelsUsed());
            printProgramPeek(programPeek, expansionDegree, true);
        } catch(SProgramNotLoadedException e){
            System.out.println("Error: Program is not loaded. Load it first (option 1)");
        }
    }
}
