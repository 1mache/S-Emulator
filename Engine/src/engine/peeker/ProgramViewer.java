package engine.peeker;

import dto.InstructionPeek;
import dto.ProgramPeek;
import engine.expansion.ProgramExpander;
import engine.instruction.Instruction;
import engine.instruction.utility.InstructionReference;
import engine.instruction.utility.Instructions;
import engine.label.Label;
import engine.program.Program;
import engine.variable.Variable;

import java.util.*;

public class ProgramViewer {
    private final Program program;

    public ProgramViewer(Program program) {
        this.program = program;
    }

    public ProgramPeek getProgramPeek(int expansionDegree) {
        return getProgramPeek(expansionDegree, null);
    }

    public ProgramPeek getProgramPeek(int expansionDegree, ProgramExpander programExpander) {
        if(programExpander == null)
            programExpander = new ProgramExpander(program);

        Program expandedProgram = programExpander.expand(expansionDegree);

        List<InstructionPeek> instructionPeeks = new ArrayList<>();

        for (int lineId = 0; lineId < expandedProgram.getInstructions().size(); lineId++) {
            List<InstructionReference> expansionChain = programExpander.getExpansionChainOf(lineId);

            // construct the expandedFrom chain
            InstructionPeek expandedFrom = null;
            for (InstructionReference instructionReference : expansionChain) {
                expandedFrom = getInstructionPeek(
                        instructionReference.instruction(),
                        instructionReference.lineId(),
                        expandedFrom
                );
            }

            // add the instruction peek to the result list
            instructionPeeks.add(getInstructionPeek(
                    expandedProgram.getInstructions().get(lineId),
                    lineId,
                    expandedFrom
            ));
        }

        return new ProgramPeek(
                program.getName(),
                getInputVariablePeeks(),
                getWorkVariablePeeks(),
                getLabelStrings(expandedProgram.getUsedLabels()),
                instructionPeeks,
                program.getArchitecture().name()
        );
    }

    public static List<Integer> idsOfInstructionsThatUse(Program program, Variable variable) {
        List<Integer> ids = new ArrayList<>();
        int id = 0;
        for (var instruction: program.getInstructions()) {
            if(Instructions.extractVariables(instruction).contains(variable))
                ids.add(id);
            id++;
        }

        return ids;
    }

    public static List<Integer> idsOfInstructionsThatUse(Program program, Label label) {
        List<Integer> ids = new ArrayList<>();
        int id = 0;
        for (var instruction: program.getInstructions()) {
            if(Instructions.extractUsedLabels(instruction).contains(label))
                ids.add(id);
            id++;
        }

        return ids;
    }


    // ===================== private =======================

    private InstructionPeek getInstructionPeek(Instruction instruction, int lineId, InstructionPeek expandedFrom) {
        return new InstructionPeek(
                instruction.stringRepresentation(),
                instruction.getLabel().stringRepresentation(),
                instruction.isSynthetic(),
                instruction.staticCycles(),
                expandedFrom,
                lineId,
                instruction.getArchitecture().name()
        );
    }

    private List<String> getInputVariablePeeks(){
        return program.getInputVariables().stream()
                .map(Variable::stringRepresentation)
                .toList();
    }

    private List<String> getWorkVariablePeeks(){
        return program.getWorkVariables().stream()
                .map(Variable::stringRepresentation)
                .toList();
    }

    private List<String> getLabelStrings(List<Label> labels){
        return labels.stream()
                .map(Label::stringRepresentation)
                .toList();
    }
}
