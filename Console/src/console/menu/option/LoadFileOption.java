package console.menu.option;

import console.menu.option.helper.ProgramName;
import engine.api.SLanguageEngine;
import engine.loader.exception.NotXMLException;
import engine.loader.exception.UnknownFunctionException;
import engine.loader.exception.UnknownLabelException;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class LoadFileOption extends MenuPage {
    public LoadFileOption() {
        super("Load program XML file", "");
    }

    @Override
    public void execute(SLanguageEngine engine, ProgramName programName) {
        System.out.println("Please enter the full path of the XML file to load: ");

        String path = getFilePath();
        if(path.isEmpty()) {
            System.out.println("Empty line entered, couldn't load file");
            return;
        }

        try{
            programName.set(engine.loadProgramFromFile(path, null).getFirst());
            System.out.println("Program loaded successfully");
        }
        catch (FileNotFoundException | NotXMLException | UnknownLabelException | UnknownFunctionException e) {
            System.out.println();
            System.out.println("Error: Couldn't load file. "+ e.getMessage());
        }
    }

    private String getFilePath() {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine().trim();
    }
}
