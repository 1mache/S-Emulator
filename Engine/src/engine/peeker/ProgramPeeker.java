package engine.peeker;

import engine.api.dto.InstructionPeek;
import engine.api.dto.ProgramPeek;
import engine.instruction.Instruction;
import engine.label.Label;
import engine.program.Program;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.*;

public class ProgramPeeker {
    private final Program program;
    private final LabelVariableGenerator labelVariableGenerator;
    private final InstructionPeek expandedFrom; // can be null

    public ProgramPeeker(Program program) {
        this(program, new LabelVariableGenerator(program),null);
    }

    // for internal use
    private ProgramPeeker(Program program, LabelVariableGenerator labelVariableGenerator, InstructionPeek expandedFrom) {
        this.program = program;
        this.labelVariableGenerator = labelVariableGenerator;
        this.expandedFrom = expandedFrom;
    }

    public ProgramPeek getProgramPeek() {
        return getProgramPeek(0);
    }

    public ProgramPeek getProgramPeek(int expansionDegree) {
        List<InstructionPeek> instructionPeekList = new ArrayList<>();
        /* preserve order, avoid dups
         include the program’s own labels (base case, no expansions)*/
        Set<String> allLabels = new LinkedHashSet<>(getLabelStrings());

        // start the line counter from the original instruction 'expandedFrom' lineId
        int lineCounter = Optional.ofNullable(expandedFrom)
                .map(InstructionPeek::lineId)
                .orElse(0);

        for (Instruction instruction : program.getInstructions()) {
            int currentLine = lineCounter;
            ProgramPeek expansionPeek = null;

            if (expansionDegree > 0) {
                expansionPeek = instruction.getExpansion(currentLine, labelVariableGenerator)
                        .map(expansion -> new ProgramPeeker(
                                expansion,
                                labelVariableGenerator,
                                getInstructionPeek(instruction, currentLine, expandedFrom)
                        ).getProgramPeek(expansionDegree - 1))
                        .orElse(null);
            }

            if (expansionPeek == null) {
                InstructionPeek peek = getInstructionPeek(instruction, currentLine, expandedFrom);
                instructionPeekList.add(peek);
                allLabels.add(instruction.getLabel().stringRepresentation());
                lineCounter++;
            } else {
                instructionPeekList.addAll(expansionPeek.instructions());
                allLabels.addAll(expansionPeek.labelsUsed()); // << merge labels from expansion
                lineCounter += expansionPeek.instructions().size();
            }
        }

        return new ProgramPeek(
                program.getName(),
                getInputVariablePeeks(),
                new ArrayList<>(allLabels),
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

    private List<String> getLabelStrings(){
        return program.getUsedLabels().stream()
                .map(Label::stringRepresentation)
                .toList();
    }
}
