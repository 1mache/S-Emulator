import console.*;
import engine.api.SLanguageEngine;

public class Main {
    public static void main(String[] args) {
        SLanguageEngine engine = SLanguageEngine.getInstance();

        MenuPage startPage = new MenuPage(
                "Start menu",
                "Welcome to S-Emulator!");

        MenuPage option1 = new LoadFileOption();
        MenuPage option2 = new PrintProgramOption();
        MenuPage option3 = new ExpandProgramOption();
        MenuPage option4 = new RunProgramOption();

        startPage.addOption(option1);
        startPage.addOption(option2);
        startPage.addOption(option3);
        startPage.addOption(option4);
        // the same menu is printed after every option
        option1.addOptionsOf(startPage);
        option2.addOptionsOf(startPage);
        option3.addOptionsOf(startPage);
        option4.addOptionsOf(startPage);

        Menu menu = new Menu(engine,startPage);
        menu.start();
    }
}