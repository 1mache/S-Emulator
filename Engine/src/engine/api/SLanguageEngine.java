package engine.api;

import engine.api.dto.InstructionPeek;
import engine.api.dto.ProgramPeek;
import engine.instruction.Instruction;
import engine.jaxb.loader.ProgramLoader;
import engine.jaxb.loader.XMLLoader;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.label.Label;
import engine.program.Program;
import engine.variable.Variable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SLanguageEngine {
    private Program program;

    public void loadProgram(String path)
            throws NotXMLException, FileNotFoundException, UnknownLabelException {
        XMLLoader loader = new ProgramLoader();
        loader.loadXML(path);

        loader.validateProgram();
        program = loader.getProgram();
    }

    public boolean isProgramLoaded(){
        return program != null;
    }

    public ProgramPeek getProgramPeek() {
        return new ProgramPeek(
                program.getName(),
                getInputVariablePeeks(),
                getLabelStrings(),
                getInstructionPeeks()
        );
    }

    private List<InstructionPeek> getInstructionPeeks() {
        List<InstructionPeek> instructionPeekList = new ArrayList<>();

        int instructionNumber = 0;
        Optional<Instruction> nextInstruction = program.getInstructionByIndex(instructionNumber);
        while(nextInstruction.isPresent()){
            Instruction instruction = nextInstruction.get();
            instructionPeekList.add(new InstructionPeek(
                    instruction.stringRepresentation(),
                    instruction.getLabel().stringRepresentation(),
                    instruction.isSynthetic(),
                    instruction.cycles(),
                    instructionNumber
            ));

            instructionNumber++;
            nextInstruction = program.getInstructionByIndex(instructionNumber);
        }
        return instructionPeekList;
    }

    private List<String> getInputVariablePeeks(){
        return program.getInputVariables().stream()
                .map(Variable::stringRepresentation)
                .toList();
    }

    private List<String> getLabelStrings(){
        return program.getLabels().stream()
                .map(Label::stringRepresentation)
                .toList();
    }
}
