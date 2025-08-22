package console;

import engine.SLanguageEngine;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class LoadFileOption extends MenuPage{
    public LoadFileOption(String name, String prompt) {
        super(name, prompt);
    }

    @Override
    public void execute(SLanguageEngine engine) {
        super.execute(engine);
        String path = getFilePath();
        if(path.isEmpty()) {
            System.out.println("Empty line entered, couldn't load file");
            return;
        }

        try{
            engine.loadProgram(path);
            System.out.println("Program loaded successfully");
        }
        catch (FileNotFoundException | NotXMLException | UnknownLabelException e) {
            System.out.println("Error: Couldn't load file. "+ e.getMessage());
        }
    }

    private String getFilePath() {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine().trim();
    }
}
