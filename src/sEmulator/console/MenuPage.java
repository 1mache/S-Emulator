package sEmulator.console;

import java.util.ArrayList;

public class MenuPage implements MenuOption {
    private ArrayList<MenuOption> options;
    private MenuOption            chosenOption;

    private final String name;
    private final String prompt;

    public MenuPage(String name, String prompt) {
        this.name = name;
        this.prompt = prompt;
        this.options = new ArrayList<>();
    }

    @Override
    public void choose() {
        System.out.println(prompt);

        if(!options.isEmpty()){
            System.out.println();
            System.out.println("Choose an option: " +
                    "(type number 1-" + options.size() + ")" );

            String optionFormat = "[%d].%s";
            for(int i = 0; i < options.size(); i++)
            {
                System.out.printf(optionFormat, i+1, options.get(i).getName());
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void addOption(MenuOption option)
    {
        options.add(option);
    }
}
