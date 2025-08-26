package engine.program;

import engine.instruction.Instruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.scanner.InstructionScanner;
import engine.variable.Variable;

import java.util.*;

public class ProgramImpl implements Program {

    private final Map<Label, InstructionReference> labeledInstructions;
    private final List<Instruction> instructions;

    private final String name;

    public ProgramImpl(String name, List<Instruction> instructions) {
        this.name = name;
        this.instructions = instructions;

        labeledInstructions = InstructionScanner.extractLabeledInstructions(instructions);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Variable> getInputVariables() {
        return InstructionScanner.extractInputVariables(instructions);
    }

    @Override
    public List<Label> getLabels() {
        return InstructionScanner.extractUsedLabels(
                labeledInstructions,
                InstructionScanner.getArgumentLabels(instructions)
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
        return Optional.ofNullable(labeledInstructions.get(label).instruction());
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