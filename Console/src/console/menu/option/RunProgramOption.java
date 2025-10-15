package console.menu.option;

import console.menu.option.helper.GlobalExecutionHistory;
import console.menu.option.helper.ProgramName;
import dto.ProgramExecutionResult;
import dto.ProgramPeek;
import engine.api.SLanguageEngine;
import engine.execution.exception.SProgramNotLoadedException;

import java.util.*;

public class RunProgramOption extends AbstractExpandingOption {
    public RunProgramOption() {
        super("Run Program", "");
    }

    @Override
    public void execute(SLanguageEngine engine, ProgramName programName) {
        try {
            int expansionDegree = getExpansionDegree(engine.getMaxExpansionDegree(programName.get()));
            ProgramPeek program = engine.getProgramPeek(
                    programName.get(), 0
            );

            ProgramPeek expanded = engine.getProgramPeek(
                    programName.get(), expansionDegree
            );

            System.out.println("The inputs that the program uses are: " + program.inputVariables());
            List<Long> inputs = getInputValues();

            System.out.println("Running program:");
            printProgramPeek(expanded, expansionDegree, false);

            ProgramExecutionResult result = engine.runProgram(
                programName.get(), expansionDegree, inputs, false, GlobalExecutionHistory.get()
            );
            System.out.println("Program execution result: " + result.outputValue());
            System.out.println("Variables that were used:");
            result.variableMap().forEach((key, value) -> System.out.println(key + "= " + value));
            System.out.println("Execution took: " + result.cycles() + " cycles.");

        } catch (SProgramNotLoadedException e) {
            System.out.println("Error: Program is not loaded. Load it first (option 1)");
        }
    }

    private List<Integer> getBadIndices(String[] parts) {
        List<Integer> badIndices = new ArrayList<>();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();

            // Check if it's a valid integer (no floats)
            try {
                if (part.contains(".")) { // floats not allowed
                    badIndices.add(i);
                } else {
                    Long.parseLong(part); // will throw if not numeric
                }
            } catch (NumberFormatException e) {
                badIndices.add(i);
            }
        }

        return badIndices;
    }

    private List<Long> getInputValues() {
        Scanner scanner = new Scanner(System.in);
        List<Long> values = new ArrayList<>();

        System.out.println("""
                Please enter input variable values separated with a ',' (e.g 1,2,3).
                Blank = No inputs
                Note that x1 will get the value of the first input, x2 will get the value of the second and so on in order:""");

        String[] parts;
        while (true) {
            String line = scanner.nextLine();
            parts = line.split(",");

            // Check validity first
            List<Integer> badIndices = getBadIndices(parts);
            if (!badIndices.isEmpty()) {
                System.out.print("Inputs: [");
                for (int idx : badIndices) {
                    System.out.print("x" + (idx + 1) + " ");
                }
                System.out.println("] are invalid (not non-negative integers). Please enter all values again:");
                continue;
            }

            // all parts parse fine here
            for (String part : parts) {
                values.add(Long.parseLong(part.trim()));
            }
            break;
        }

        return values;
    }

}
