package console;

import engine.api.SLanguageEngine;
import engine.api.dto.ProgramPeek;
import engine.execution.exception.SProgramNotLoadedException;


public class PrintProgramOption extends MenuPage {
    public PrintProgramOption() {
        super("Print Program", "");
    }

    @Override
    public void execute(SLanguageEngine engine) {
        ProgramPeek programPeek;
        try {
            programPeek = engine.getProgramPeek();
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
