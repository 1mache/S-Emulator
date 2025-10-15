package console.menu.option;

import console.menu.option.helper.ProgramName;
import dto.ProgramPeek;
import engine.api.SLanguageEngine;
import engine.execution.exception.SProgramNotLoadedException;


public class PrintProgramOption extends MenuPage {
    public PrintProgramOption() {
        super("Print Program", "");
    }

    @Override
    public void execute(SLanguageEngine engine, ProgramName programName) {
        ProgramPeek programPeek;
        try {
            programPeek = engine.getProgramPeek(
                    programName.get(), 0
            );
        } catch (SProgramNotLoadedException e) {
            System.out.println("Error: Program is not loaded. Load it first (option 1)");
            return;
        }
        System.out.println("Program name: " + programPeek.name());
        System.out.println("Input variables used: " + programPeek.inputVariables());
        System.out.println("Labels used: " + programPeek.labelsUsed());

        printProgramPeek(programPeek, 0, false);
    }
}
