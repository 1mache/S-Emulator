package engine.program;

import engine.instruction.Instruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.instruction.utility.Instructions;
import engine.variable.Variable;

import java.util.*;

public class ProgramImpl implements Program {

    private final Map<Label, InstructionReference> labeledInstructions;
    private final List<Instruction> instructions;

    private final String name;

    public ProgramImpl(String name, List<Instruction> instructions) {
        this.name = name;
        this.instructions = instructions;

        labeledInstructions = Instructions.extractLabeledInstructions(instructions);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Variable> getInputVariables() {
        return Instructions.extractInputVariables(instructions);
    }

    @Override
    public List<Variable> getWorkVariables() {
        return Instructions.extractWorkVariables(instructions);
    }

    @Override
    public List<Label> getUsedLabels() {
        return Instructions.extractUsedLabels(
                labeledInstructions,
                Instructions.getArgumentLabels(instructions)
        );
    }

    @Override
    public boolean hasLabel(Label label) {
        return label == FixedLabel.EMPTY || labeledInstructions.containsKey(label);
    }

    @Override
    public Optional<Instruction> getInstruction(Label label) {
        if(label == FixedLabel.EMPTY)
            return Optional.empty();
        return Optional.ofNullable(labeledInstructions.get(label))
                .map(InstructionReference::instruction);
    }

    @Override
    public Optional<Integer> getLabelLineId(Label label) {
        if(label == FixedLabel.EMPTY)
            return Optional.empty();
        return Optional.ofNullable(labeledInstructions.get(label))
                .map(InstructionReference::lineId);
    }

    @Override
    public Optional<Instruction> getInstructionByIndex(int index) {
        if(index >= instructions.size())
            return Optional.empty();
        return Optional.of(instructions.get(index));
    }

    @Override
    public List<Instruction> getInstructions() {
        return instructions;
    }
}