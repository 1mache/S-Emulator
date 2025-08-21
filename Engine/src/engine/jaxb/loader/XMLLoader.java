package engine.jaxb.loader;

import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.program.Program;

import java.io.FileNotFoundException;

public interface XMLLoader {
    void loadXML(String path) throws FileNotFoundException, NotXMLException;
    void validateProgram() throws UnknownLabelException;
    Program getProgram();
}
