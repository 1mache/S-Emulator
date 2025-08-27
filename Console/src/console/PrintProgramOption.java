package console;

import engine.api.SLanguageEngine;
import engine.api.dto.InstructionPeek;
import engine.api.dto.ProgramPeek;

import java.util.List;

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

        printInstructions(programPeek.instructions());
    }

    private void printInstructions(List<InstructionPeek> instructions) {
        for (InstructionPeek instr : instructions) {
            // lineId
            String number = "#" + (instr.lineId() + 1); // +1 so it starts with 1

            // synthetic or base
            String type = instr.isSynthetic() ? "(S)" : "(B)";

            // label formatting: 5 characters wide, label starts at position 2
            String formattedLabel = String.format("[ %-4s]", instr.label()); // left-padded, width 5;

            // instruction string
            String instrString = instr.stringRepresentation();

            // cycles in parentheses
            String cycles = "(" + instr.cycles() + ")";

            System.out.printf("%s %s %s %s %s%n", number, type, formattedLabel, instrString, cycles);
        }
    }
}
