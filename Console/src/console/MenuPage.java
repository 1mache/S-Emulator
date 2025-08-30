package console;

import engine.api.SLanguageEngine;
import engine.api.dto.InstructionPeek;
import engine.api.dto.ProgramPeek;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class MenuPage implements MenuOption {
    private final ArrayList<MenuOption> options;

    private final String name;
    private final String message;

    private static class ExitOption implements MenuOption {
        @Override
        public void execute(SLanguageEngine engine) {}

        @Override
        public String getName() {
            return "Exit";
        }

        @Override
        public MenuOption getNextOption() {
            return null;
        }
    }

    // back option. will always be last in the options list
    public static final MenuOption EXIT_OPTION = new ExitOption();

    // public:
    public MenuPage(String name, String message) {
        this.name = name;
        this.message = message;
        this.options = new ArrayList<>(1);
        options.add(EXIT_OPTION);
    }

    @Override
    public void execute(SLanguageEngine engine) {
        System.out.println(message);
    }

    @Override
    public String getName() {
        return name;
    }

    public MenuOption getNextOption() {
        System.out.println();
        System.out.println("Choose an option: " +
                    "(type number 1-" + options.size() + ")" );

        String optionFormat = "[%d].%s";
        for(int i = 0; i < options.size(); i++)
        {
            System.out.printf(optionFormat + '\n', i+1, options.get(i).getName());
        }

        return options.get(askForOptionId());
    }

    public void addOption(MenuOption option)
    {
        // add the new option as last before the back option
        options.add(options.size()-1,option);
    }

    public void addOptionsOf(MenuPage menuPage){
        var otherOptions = menuPage.options;
        if (otherOptions.size() > 1) {
            // Create a sublist excluding the last element (EXIT)
            List<MenuOption> toAdd = otherOptions.subList(0, otherOptions.size() - 1);
            // Add all at the beginning of the 'options' list
            options.addAll(0, toAdd);
        }
    }

    protected void printInstruction(InstructionPeek instruction) {
        // lineId
        String number = "#" + (instruction.lineId() + 1); // +1 so it starts with 1

        // synthetic or base
        String type = instruction.isSynthetic() ? "(S)" : "(B)";

        // label formatting: 5 characters wide, label starts at position 2
        String formattedLabel = String.format("[ %-4s]", instruction.label()); // left-padded, width 5;

        // instruction string
        String instrString = instruction.stringRepresentation();

        // cycles in parentheses
        String cycles = "(" + instruction.cycles() + ")";

        System.out.printf("%s %s %s %s %s", number, type, formattedLabel, instrString, cycles);
    }

    protected void printProgramPeek(ProgramPeek programPeek, int expansionDegree, boolean printHierarchy) {
        for(var instruction : programPeek.instructions()){
            InstructionPeek expandedFrom = instruction.expandedFrom();
            printInstruction(instruction);

            if(printHierarchy) {
                for (int i = 0; i < expansionDegree && expandedFrom != null; i++) {
                    System.out.print(" <<< ");
                    printInstruction(expandedFrom);
                }
            }

            System.out.println();
        }
    }

    private int askForOptionId() {
        Scanner scanner = new Scanner(System.in);
        int optionId = -1;

        while (invalidOptionId(optionId)) {
            try {
                int input = scanner.nextInt();
                optionId = input - 1;
                if (invalidOptionId(optionId)) {
                    System.out.printf("Invalid option number. Please try again.%n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine();
            }
        }

        return optionId;
    }

    private boolean invalidOptionId(int optionId) {
        // check positive and in bounds
        return (optionId < 0 || optionId >= options.size());
    }
}
