package sEmulator;

import sEmulator.console.Menu;
import sEmulator.console.MenuPage;

public class Main {
    public static void main(String[] args) {
        MenuPage startPage = new MenuPage(
                 "Start menu",
                "Hi, twin, welcome to my awesome Jaba program! \n" +
                        "How are you twin <3");

        MenuPage page1 = new MenuPage(
                 "Im good thanks...",
                "Glad to hear that. Now tickle me <3");

        MenuPage page2 = new MenuPage(
                 "Who are you?",
                "Last name Smart, first name Menu, middle name..." +
                        "Danger ...");

        startPage.addOption(page1);
        startPage.addOption(page2);

        Menu menu = new Menu(startPage);
        menu.start();
    }
}
