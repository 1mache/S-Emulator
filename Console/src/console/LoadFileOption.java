package console;

import engine.api.SLanguageEngine;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class LoadFileOption extends MenuPage{
    public LoadFileOption() {
        super("Load program XML file", "");
    }

    @Override
    public void execute(SLanguageEngine engine) {
        System.out.println("Please enter the full path of the XML file to load: ");

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
            System.out.println();
            System.out.println("Error: Couldn't load file. "+ e.getMessage());
        }
    }

    private String getFilePath() {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine().trim();
    }
}
