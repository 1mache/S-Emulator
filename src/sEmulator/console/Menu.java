package sEmulator.console;

public class Menu {
    private final MenuPage startPage;

    public Menu(MenuPage startPage) {
        this.startPage = startPage;
    }

    public void start()
    {
        startPage.select();
        MenuOption option = startPage.getNextOption();

        // while option can be selected and doesn't close menu
        while(option != null && !option.closesMenu()) {
            option.select(); // keep going
            option = option.getNextOption();
        }

        if(option != null)
            option.select(); // we got here meaning option closes menu
    }
}
