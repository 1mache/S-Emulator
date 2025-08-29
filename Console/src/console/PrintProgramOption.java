package console;

import engine.api.SLanguageEngine;
import engine.api.dto.ProgramPeek;


public class PrintProgramOption extends MenuPage {
    public PrintProgramOption() {
        super("Print Program", "");
    }

    @Override
    public void execute(SLanguageEngine engine) {
        if(engine.programNotLoaded()){
            System.out.println("Error: Program Not Loaded!");
            return;
        }

        ProgramPeek programPeek = engine.getProgramPeek();
        System.out.println("Program name: " + programPeek.name());
        System.out.println("Input variables used: " + programPeek.inputVariables());
        System.out.println("Labels used: " + programPeek.labelsUsed());

        printProgramPeek(programPeek, 0);
    }
}
