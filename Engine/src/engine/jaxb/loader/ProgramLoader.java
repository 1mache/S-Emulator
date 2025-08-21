package engine.jaxb.loader;

import engine.jaxb.generated.SProgram;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.SProgramXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.label.Label;
import engine.program.Program;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.util.List;

public class ProgramLoader implements XMLLoader{
    private Program program;
    private List<Label> argumentLabels;

    @Override
    public void loadXML(String path) throws FileNotFoundException, NotXMLException{
        if(!path.endsWith(".xml"))
            throw new NotXMLException("Path "+ path + " does not lead to an xml file");
        try {
            SProgram sProgram = JaxbLoader.loadProgramFromXML(path);
            JaxbTranslator translator = new JaxbTranslator();
            program = translator.getProgram(sProgram);
            argumentLabels = translator.getArgumentLabels();
        } catch (JAXBException e) { // should never happen
            throw new SProgramXMLException("Failed to marshal XML.", e);
        }
    }

    @Override
    public void validateProgram() throws UnknownLabelException {
        for(Label label : argumentLabels){
            if(!program.hasLabel(label))
                throw new UnknownLabelException("Error: Unknown label "+ label.stringRepresentation());
        }
    }

    @Override
    public Program getProgram() {
        return program;
    }
}
