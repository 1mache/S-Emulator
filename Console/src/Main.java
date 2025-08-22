import console.LoadFileOption;
import console.Menu;
import console.MenuPage;
import engine.SLanguageEngine;

public class Main {
    public static void main(String[] args) {
        SLanguageEngine engine = new SLanguageEngine();

        MenuPage startPage = new MenuPage(
                "Start menu",
                "Hi, twin, welcome to my awesome Jaba program! \n" +
                        "Please select an option: <3");

        MenuPage option1 = new LoadFileOption(
                "Load Program", "Please enter the full path to file: "
        );

        startPage.addOption(option1);
        option1.addOption(option1);

        Menu menu = new Menu(engine,startPage);
        menu.start();
    }
}