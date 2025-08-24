import console.LoadFileOption;
import console.Menu;
import console.MenuPage;
import console.PrintProgramOption;
import engine.api.SLanguageEngine;

public class Main {
    public static void main(String[] args) {
        SLanguageEngine engine = new SLanguageEngine();

        MenuPage startPage = new MenuPage(
                "Start menu",
                "Hi, twin, welcome to my awesome Jaba program! \n" +
                        "Please select an option: ");

        MenuPage option1 = new LoadFileOption();
        MenuPage option2 = new PrintProgramOption();

        startPage.addOption(option1);
        startPage.addOption(option2);
        option1.addOptionsOf(startPage);
        option2.addOptionsOf(startPage);

        Menu menu = new Menu(engine,startPage);
        menu.start();
    }
}