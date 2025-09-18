package engine.peeker;

import engine.api.dto.InstructionPeek;
import engine.api.dto.ProgramPeek;
import engine.expansion.ProgramExpander;
import engine.instruction.Instruction;
import engine.instruction.utility.InstructionReference;
import engine.label.Label;
import engine.program.Program;
import engine.variable.Variable;

import java.util.*;

public class ProgramViewer {
    private final Program program;

    public ProgramViewer(Program program) {
        this.program = program;
    }

    public ProgramPeek getProgramPeek(int expansionDegree){
        var expander = new ProgramExpander(program);
        Program expandedProgram = expander.expand(expansionDegree);

        List<InstructionPeek> instructionPeeks = new ArrayList<>();

        for (int lineId = 0; lineId < expandedProgram.getInstructions().size(); lineId++) {
            List<InstructionReference> expansionChain = expander.getExpansionChainOf(lineId);

            // construct the expandedFrom chain
            InstructionPeek expandedFrom = null;
            for (InstructionReference instructionReference : expansionChain) {
                expandedFrom = getInstructionPeek(
                        instructionReference.instruction(),
                        instructionReference.lineId(),
                        expandedFrom
                );
            }

            instructionPeeks.add(getInstructionPeek(
                    expandedProgram.getInstructions().get(lineId),
                    lineId,
                    expandedFrom
            ));
        }

        return new ProgramPeek(
                program.getName(),
                getInputVariablePeeks(),
                getLabelStrings(expandedProgram.getUsedLabels()),
                instructionPeeks
        );
    }


    private InstructionPeek getInstructionPeek(Instruction instruction, int lineId, InstructionPeek expandedFrom) {
        return new InstructionPeek(
                instruction.stringRepresentation(),
                instruction.getLabel().stringRepresentation(),
                instruction.isSynthetic(),
                instruction.cycles(),
                expandedFrom,
                lineId
        );
    }

    private List<String> getInputVariablePeeks(){
        return program.getInputVariables().stream()
                .map(Variable::stringRepresentation)
                .toList();
    }

    private List<String> getLabelStrings(List<Label> labels){
        return labels.stream()
                .map(Label::stringRepresentation)
                .toList();
    }
}
