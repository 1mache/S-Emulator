package sEmulator.console;

import java.util.ArrayList;
import java.util.Scanner;

public class MenuPage implements MenuOption {
    private final ArrayList<MenuOption> options;

    private final String name;
    private final String prompt;

    // public:
    public MenuPage(String name, String prompt) {
        this.name = name;
        this.prompt = prompt;
        this.options = new ArrayList<>();
    }

    @Override
    public void select() {
        System.out.println(prompt);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean closesMenu() {
        return false; // menu page doesn't close menu when proceed() is called
    }

    public MenuOption getNextOption() {
        if(options.isEmpty())
            return null; // this menu has no options

        System.out.println();
        System.out.println("Choose an option: " +
                    "(type number 1-" + options.size() + ")" );

        String optionFormat = "[%d].%s";
        for(int i = 0; i < options.size(); i++)
        {
            System.out.printf(optionFormat + '\n', i+1, options.get(i).getName());
        }

        int optionId = askForId();
        return options.get(optionId);
    }

    public void addOption(MenuOption option)
    {
        options.add(option);
    }

    private int askForId(){
        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        int optionId = input-1; // user counts them from 1
        while (!checkValidOptionId(optionId))
        {
            System.out.printf("Invalid option. Please try again.%n");
            input = scanner.nextInt();
            optionId = input-1;
        }

        return optionId;
    }

    private boolean checkValidOptionId(int optionId) {
        // check positive and in bounds
        return (optionId >= 0 && optionId < options.size());
    }
}
