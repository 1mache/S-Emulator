package console.menu;

import console.menu.option.MenuOption;
import console.menu.option.MenuPage;
import console.menu.option.helper.ProgramName;
import engine.api.SLanguageEngine;

public class Menu {
    private final SLanguageEngine engine;
    private final ProgramName programName;
    private final MenuPage startPage;

    public Menu(SLanguageEngine engine, MenuPage startPage) {
        this.engine = engine;
        this.startPage = startPage;
        programName = new ProgramName();
    }

    public void start()
    {
        startPage.execute(engine, programName); // welcome message
        MenuOption option = startPage.getNextOption();

        // while option can be selected and doesn't close menu
        while(option != MenuPage.EXIT_OPTION) {
            option.execute(engine, programName); // keep going
            option = option.getNextOption();
        }
    }
}
