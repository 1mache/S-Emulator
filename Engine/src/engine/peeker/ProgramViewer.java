package engine.peeker;

import engine.api.dto.InstructionPeek;
import engine.api.dto.ProgramPeek;
import engine.instruction.Instruction;
import engine.label.Label;
import engine.program.Program;
import engine.program.generator.LabelVariableGenerator;
import engine.instruction.utility.Instructions;
import engine.variable.Variable;

import java.util.*;

public class ProgramViewer {
    private final Program program;
    private final LabelVariableGenerator labelVariableGenerator;
    private final InstructionPeek expandedFrom; // can be null

    public ProgramViewer(Program program) {
        this(program, new LabelVariableGenerator(program),null);
    }

    // for internal use
    private ProgramViewer(Program program, LabelVariableGenerator labelVariableGenerator, InstructionPeek expandedFrom) {
        this.program = program;
        this.labelVariableGenerator = labelVariableGenerator;
        this.expandedFrom = expandedFrom;
    }

    public ProgramPeek getProgramPeek(int expansionDegree){
        return getProgramPeekRec(expansionDegree, 0, new ArrayList<>());
    }

    // "recursive" version
    private ProgramPeek getProgramPeekRec(int expansionDegree, int lineCounter, List<Instruction> allInstructions) {
        List<InstructionPeek> instructionPeekList = new ArrayList<>();
        List<Instruction> baseInstructions = program.getInstructions();
        allInstructions.addAll(baseInstructions);

        // start the line counter from the original instruction 'expandedFrom' lineId
        int instructionCount = Optional.ofNullable(expandedFrom)
                .map(InstructionPeek::lineId)
                .orElse(0);

        for (Instruction instruction : baseInstructions) {
            int currentLine = lineCounter;

            ProgramPeek expansionPeek = null;

            if (expansionDegree > 0) {
                // this is the "root peek" for this instruction at the current level
                InstructionPeek basePeek = getInstructionPeek(instruction, instructionCount, expandedFrom);

                expansionPeek = instruction.getExpansion(labelVariableGenerator)
                        .map(expansion -> {
                            allInstructions.addAll(expansion.getInstructions());

                            return new ProgramViewer(
                                    expansion,
                                    labelVariableGenerator,
                                    basePeek
                            ).getProgramPeekRec(expansionDegree - 1, currentLine, allInstructions);
                        })
                        .orElse(null);
            }

            if (expansionPeek == null) {
                InstructionPeek peek = getInstructionPeek(instruction, currentLine, expandedFrom);
                instructionPeekList.add(peek);
                lineCounter++;
            } else {
                instructionPeekList.addAll(expansionPeek.instructions());
                lineCounter += expansionPeek.instructions().size();
            }

            instructionCount++;
        }

        return new ProgramPeek(
                program.getName(),
                getInputVariablePeeks(),
                getLabelStrings(Instructions.extractUsedLabels(allInstructions)),
                instructionPeekList
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
