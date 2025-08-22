package console;

import engine.SLanguageEngine;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuPage implements MenuOption {
    private final ArrayList<MenuOption> options;

    private final String name;
    private final String prompt;

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
    public MenuPage(String name, String prompt) {
        this.name = name;
        this.prompt = prompt;
        this.options = new ArrayList<>(1);
        options.add(EXIT_OPTION);
    }

    @Override
    public void execute(SLanguageEngine engine) {
        System.out.println(prompt);
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
