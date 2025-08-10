package sEmulator.console;

import java.util.LinkedList;

public class Menu {
    private final MenuPage startPage;
    private final LinkedList<MenuOption> choiceStack;


    public Menu(MenuPage startPage) {
        this.startPage = startPage;
        choiceStack = new LinkedList<>();
        choiceStack.addFirst(startPage);
    }

    public void start()
    {
        startPage.select();
        MenuOption option = startPage.getNextOption();

        // while option can be selected and doesn't close menu
        while(option != null && !option.closesMenu()) {
            if(option == MenuPage.BACK_OPTION){
                if(choiceStack.size() <= 1) return; // exit
                choiceStack.pop(); // go back 1 option
                option = choiceStack.pop();
                continue;
            }

            choiceStack.addFirst(option);
            option.select(); // keep going
            option = option.getNextOption();
        }

        if(option != null)
            option.select(); // we got here meaning option closes menu
    }
}
