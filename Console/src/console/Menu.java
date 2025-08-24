package console;

import engine.api.SLanguageEngine;

public class Menu {
    private final SLanguageEngine engine;
    private final MenuPage startPage;

    public Menu(SLanguageEngine engine, MenuPage startPage) {
        this.engine = engine;
        this.startPage = startPage;
    }

    public void start()
    {
        startPage.execute(engine);
        MenuOption option = startPage.getNextOption();

        // while option can be selected and doesn't close menu
        while(option != MenuPage.EXIT_OPTION) {
            option.execute(engine); // keep going
            option = option.getNextOption();
        }
    }
}
