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
    // all instructions that we've seen, to extract labels
    private final Set<Instruction> allInstructions;

    public ProgramViewer(Program program) {
        this(program, new LabelVariableGenerator(program),null, new HashSet<>());
    }

    // for internal use
    private ProgramViewer(
            Program program,
            LabelVariableGenerator labelVariableGenerator,
            InstructionPeek expandedFrom,
            Set<Instruction> allInstructions
    ) {
        this.program = program;
        this.labelVariableGenerator = labelVariableGenerator;
        this.expandedFrom = expandedFrom;
        this.allInstructions = allInstructions;
    }

    public ProgramPeek getProgramPeek(int expansionDegree){
        return getProgramPeekRec(expansionDegree, 0);
    }

    // "recursive" version
    private ProgramPeek getProgramPeekRec(int expansionDegree, int globalLineCount) {
        List<InstructionPeek> instructionPeekList = new ArrayList<>();
        List<Instruction> baseInstructions = program.getInstructions();

        /* start the instruction counter from the original instructions lineId.
           if the original instruction was at line 5, we want the expansion to
            start at line 5 */
        int baseInstructionId = Optional.ofNullable(expandedFrom)
                .map(InstructionPeek::lineId)
                .orElse(0);

        for (Instruction instruction : baseInstructions) {
            int currentLine = globalLineCount;
            // this is the "root peek" for this instruction at the current level
            InstructionPeek basePeek = getInstructionPeek(instruction, baseInstructionId, expandedFrom);

            ProgramPeek expansionPeek = null;

            if (expansionDegree > 0) {
                expansionPeek = instruction.getExpansionInProgram(labelVariableGenerator)
                        .map(expansion -> {
                            allInstructions.addAll(expansion.getInstructions());

                            return new ProgramViewer(
                                    expansion,
                                    labelVariableGenerator,
                                    basePeek,
                                    allInstructions
                            ).getProgramPeekRec(expansionDegree - 1, currentLine);
                        })
                        .orElse(null);
            }

            if (expansionPeek == null) {
                InstructionPeek peek = getInstructionPeek(instruction, currentLine, expandedFrom);
                instructionPeekList.add(peek);
                globalLineCount++;
            } else {
                instructionPeekList.addAll(expansionPeek.instructions());
                globalLineCount += expansionPeek.instructions().size();
            }

            baseInstructionId++;
        }

        return new ProgramPeek(
                program.getName(),
                getInputVariablePeeks(),
                getLabelStrings(Instructions.extractUsedLabels(allInstructions.stream().toList())),
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
