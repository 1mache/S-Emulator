package console.menu.option;

import engine.api.SLanguageEngine;

public interface MenuOption {
    /**
     * what happens when an option is selected
     */
    void execute(SLanguageEngine engine);

    /**
     * @return name of the option to display in menu
     */
    String getName();

    /**
     * if this option includes other options in it we can request next option
     * @return next option to be loaded
     */
    MenuOption getNextOption();
}
