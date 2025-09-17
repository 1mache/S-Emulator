package engine.peeker;

import engine.api.dto.InstructionPeek;
import engine.api.dto.ProgramPeek;
import engine.expander.ProgramExpander;
import engine.instruction.Instruction;
import engine.label.Label;
import engine.program.Program;
import engine.program.generator.LabelVariableGenerator;
import engine.instruction.utility.Instructions;
import engine.variable.Variable;

import java.util.*;

public class ProgramViewer {
    private final Program program;
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
        this.expandedFrom = expandedFrom;
        this.allInstructions = allInstructions;
    }

    public ProgramPeek getProgramPeek(int expansionDegree){
        Program expandedProgram = new ProgramExpander(program).expand(expansionDegree);
        List<InstructionPeek> instructionPeeks = new ArrayList<>();
        for (var instruction : expandedProgram.getInstructions()) {
            int lineId = 0; // TODO: for now
            instructionPeeks.add(getInstructionPeek(instruction, lineId, expandedFrom));
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
