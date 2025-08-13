package console;

public interface MenuOption {
    /**
     * what happens when an option is selected
     */
    void select();

    /**
     * @return name of the option to display in menu
     */
    String  getName();

    /**
     * if this option includes other options in it we can request next option
     * @return next option to be loaded
     */
    MenuOption getNextOption();

    /**
     * @return true if this option's selection is supposed to result
     *         in menu being closed
     */
    boolean closesMenu();
}
