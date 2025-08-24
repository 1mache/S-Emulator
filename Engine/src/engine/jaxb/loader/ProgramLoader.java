package engine.jaxb.loader;

import engine.jaxb.generated.SProgram;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.SProgramXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.program.scanner.InstructionScanner;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.util.List;

public class ProgramLoader implements XMLLoader{
    private Program program;

    @Override
    public void loadXML(String path) throws FileNotFoundException, NotXMLException{
        if(!path.endsWith(".xml"))
            throw new NotXMLException(path + " is not an xml file");
        try {
            SProgram sProgram = JaxbLoader.loadProgramFromXML(path);
            JaxbTranslator translator = new JaxbTranslator();
            program = translator.getProgram(sProgram);
        } catch (JAXBException e) { // should never happen
            throw new SProgramXMLException("Failed to marshal XML.", e);
        }
    }

    @Override
    public void validateProgram() throws UnknownLabelException {
        List<ArgumentLabelInfo> argumentLabels = InstructionScanner.getArgumentLabels(program.getInstructions());
        for(var info : argumentLabels){
            if(info.label() != FixedLabel.EXIT && !program.hasLabel(info.label()))
                throw new UnknownLabelException("Error: Unknown label "
                        + info.label().stringRepresentation()
                        + " in Instruction: " + info.instructionName());
        }
    }

    @Override
    public Program getProgram() {
        return program;
    }
}
