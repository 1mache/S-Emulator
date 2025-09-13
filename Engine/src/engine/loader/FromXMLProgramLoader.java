package engine.loader;

import engine.jaxb.generated.SProgram;
import engine.loader.event.LoadingListener;
import engine.loader.exception.NotXMLException;
import engine.loader.exception.SProgramXMLException;
import engine.loader.exception.UnknownLabelException;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.instruction.utility.Instructions;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.util.List;

public class FromXMLProgramLoader {
    private Program program; // if this is null, the program isn't loaded
    private boolean validated = false;

    public void loadXML(String path, LoadingListener listener) throws FileNotFoundException, NotXMLException{
        if(!path.endsWith(".xml"))
            throw new NotXMLException(path + " is not an xml file");
        try {
            SProgram sProgram = JaxbLoader.loadProgramFromXML(path);
            JaxbTranslator translator = new JaxbTranslator();
            program = translator.getProgram(sProgram, listener);
        } catch (JAXBException e) { // should never happen
            throw new SProgramXMLException("Failed to marshal XML.", e);
        }
    }

    public void validateProgram() throws UnknownLabelException {
        validated = false;

        List<ArgumentLabelInfo> argumentLabels = Instructions.getArgumentLabels(program.getInstructions());
        for(var info : argumentLabels){
            if(info.label() != FixedLabel.EXIT && !program.hasLabel(info.label()))
                 throw new UnknownLabelException("Error: Unknown label "
                        + info.label().stringRepresentation()
                        + " in Instruction: " + info.instructionName());
        }

        validated = true;
    }

    public Program getProgram() {
        if(program == null)
            throw new IllegalStateException("Program has not been loaded");
        if(!validated)
            throw new IllegalStateException("Program has not been validated");
        return program;
    }
}
