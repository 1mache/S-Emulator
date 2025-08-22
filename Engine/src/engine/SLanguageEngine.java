package engine;

import engine.jaxb.loader.ProgramLoader;
import engine.jaxb.loader.XMLLoader;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.program.Program;

import java.io.FileNotFoundException;

public class SLanguageEngine {
    private Program program;

    public void loadProgram(String path)
            throws NotXMLException, FileNotFoundException, UnknownLabelException {
        XMLLoader loader = new ProgramLoader();
        loader.loadXML(path);

        loader.validateProgram();
        program = loader.getProgram();
    }
}
