package engine.program;

import engine.instruction.Instruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.scanner.InstructionScanner;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramImpl implements Program {

    // record that represents instruction and its ordinal id in the program
    public record InstructionLocator(Instruction instruction, int lineId) {
        public static InstructionLocator EXIT_LOC = new InstructionLocator(null, 0);
    }

    private final Map<Label, InstructionLocator> labeledInstructions;
    private final List<Instruction> instructions;
    private final List<Variable> inputVariables;
    private final List<Label> usedLabels;

    private final String name;

    public ProgramImpl(String name, List<Instruction> instructions, boolean usesExit) {
        this.name = name;
        this.instructions = instructions;

        labeledInstructions = InstructionScanner.extractLabeledInstructions(instructions);
        inputVariables = InstructionScanner.extractInputVariables(instructions);
        if(usesExit)
            labeledInstructions.put(FixedLabel.EXIT, InstructionLocator.EXIT_LOC);

        usedLabels = InstructionScanner.extractUsedLabels(labeledInstructions);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Variable> getInputVariables() {
        return inputVariables;
    }

    @Override
    public List<Label> getLabels() {
        return usedLabels;
    }

    @Override
    public boolean hasLabel(Label label) {
        return label == FixedLabel.EMPTY || labeledInstructions.containsKey(label);
    }

    @Override
    public Optional<Instruction> getInstruction(Label label) {
        if(label == FixedLabel.EMPTY)
            return Optional.empty();
        return Optional.of(labeledInstructions.get(label).instruction());
    }

    @Override
    public Optional<Integer> getLabelLineId(Label label) {
        if(label == FixedLabel.EMPTY)
            return Optional.empty();
        return Optional.of(labeledInstructions.get(label).lineId());
    }

    @Override
    public Optional<Instruction> getInstructionByIndex(int index) {
        if(index >= instructions.size())
            return Optional.empty();
        return Optional.of(instructions.get(index));
    }
}