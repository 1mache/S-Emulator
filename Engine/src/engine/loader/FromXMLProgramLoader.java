package engine.loader;

import engine.jaxb.generated.SProgram;
import engine.loader.event.LoadingListener;
import engine.loader.exception.*;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.instruction.utility.Instructions;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FromXMLProgramLoader {
    private Program program; // if this is null, the program isn't loaded
    private boolean validated = false;

    private JaxbTranslator translator;

    public void loadXML(String path, LoadingListener listener) throws FileNotFoundException, NotXMLException{
        if(!path.endsWith(".xml"))
            throw new NotXMLException(path + " is not an xml file");
        try {
            SProgram sProgram = JaxbLoader.loadProgramFromXML(path);
            translator = new JaxbTranslator();
            program = translator.getProgram(sProgram, listener);
        } catch (JAXBException e) { // should never happen
            throw new SProgramXMLException("Failed to marshal XML.", e);
        } catch (DuplicateProgramException e) {
            throw new RuntimeException(e); // should never get here because no additional functions are present
        }
    }

    public void loadXML(InputStream inputStream, LoadingListener listener, Map<String, Program> availableExternalPrograms)
            throws DuplicateProgramException {
        try {
            SProgram sProgram = JaxbLoader.loadProgramFromXML(inputStream);
            translator = new JaxbTranslator();
            program = translator.getProgram(sProgram, listener, availableExternalPrograms);
        } catch (JAXBException e) {
            throw new SProgramXMLException("Failed to marshal XML.", e);
        }
    }

    public void validateProgram() throws UnknownLabelException, UnknownFunctionException {
        if(program == null)
            throw new IllegalStateException("Program has not been loaded");
        // the translator is initialized too at this point. see loadXML

        validated = false;

        // check if all the labels used by the Instructions actually exist
        List<ArgumentLabelInfo> argumentLabels = Instructions.getArgumentLabels(program.getInstructions());
        for(var info : argumentLabels){
            if(info.label() != FixedLabel.EXIT && !program.hasLabel(info.label()))
                 throw new UnknownLabelException("Unknown label "
                        + info.label().stringRepresentation()
                        + " in Instruction: " + info.instructionName());
        }

        // check if all the function references were resolved
        for(var functionRef: translator.getFunctionCalls()){
            if(functionRef.getFunction() == null){
                throw new UnknownFunctionException("Unknown function used: " + functionRef.getReferralName());
            }
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

    public Set<Program> getFunctions() {
        if(program == null)
            throw new IllegalStateException("Program has not been loaded");
        if(!validated)
            throw new IllegalStateException("Program has not been validated");

        return new HashSet<>(translator.getFunctions());
    }
}
