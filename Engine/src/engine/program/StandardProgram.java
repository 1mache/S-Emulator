package engine.program;

import engine.instruction.Instruction;
import engine.instruction.utility.InstructionReference;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.instruction.utility.Instructions;
import engine.loader.ArgumentLabelInfo;
import engine.variable.Variable;

import java.util.*;

public class StandardProgram implements Program {

    private final Map<Label, InstructionReference> labeledInstructions;
    private final List<Instruction> instructions;

    private final String name;

    // cached because calculation is expensive
    private Integer maxExpansionDegree;

    public StandardProgram(String name, List<Instruction> instructions) {
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
        List<Label> instructionLabels = new ArrayList<>(
                labeledInstructions.keySet().stream()
                        .sorted(Label.comparator())
                        .toList()
        );

        if(Instructions.getArgumentLabels(instructions).stream()
                .map(ArgumentLabelInfo::label)
                .anyMatch(label -> label.equals(FixedLabel.EXIT))
          )
            instructionLabels.add(FixedLabel.EXIT);

        return instructionLabels;
    }

    @Override
    public boolean hasLabel(Label label) {
        return label == FixedLabel.EMPTY || labeledInstructions.containsKey(label);
    }

    @Override
    public Optional<Instruction> getInstructionByLabel(Label label) {
        if(label == FixedLabel.EMPTY)
            return Optional.empty();
        return Optional.ofNullable(labeledInstructions.get(label))
                .map(InstructionReference::instruction);
    }

    @Override
    public Optional<Integer> getLineNumberOfLabel(Label label) {
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
        return instructions.stream().toList();
    }

    @Override
    public int getMaxExpansionDegree() {
        if(maxExpansionDegree == null)
            maxExpansionDegree = calculateMaxExpansionDegree();

        return maxExpansionDegree;
    }

    private int calculateMaxExpansionDegree() {
        maxExpansionDegree = 0;

        for(Instruction instruction : instructions) {
            int expansionDegree = instruction.getExpansion()
                    .map(expansion -> expansion.getMaxExpansionDegree() + 1)
                    .orElse(0);

            maxExpansionDegree = Math.max(maxExpansionDegree, expansionDegree);
        }

        return maxExpansionDegree;
    }
}