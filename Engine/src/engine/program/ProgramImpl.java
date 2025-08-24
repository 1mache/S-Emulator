package engine.program;

import engine.instruction.Instruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramImpl implements Program {

    // record that represents instruction and its ordinal id in the program
    private record InstructionLocator(Instruction instruction, int lineId) {
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
        labeledInstructions = extractLabeledInstructions(instructions);
        inputVariables = extractInputVariables(instructions);
        usedLabels = extractUsedLabels(usesExit);

        if(usesExit)
            labeledInstructions.put(FixedLabel.EXIT, InstructionLocator.EXIT_LOC);
    }

    @Override
    public String getName() {
        return name;
    }

    // TODO: change to format required

    @Override
    public String print() {
        StringBuilder result = new StringBuilder();
        for(var instruction : instructions) {
            if(!instruction.getLabel().equals(FixedLabel.EMPTY))
                result.append("[").append(instruction.getLabel().stringRepresentation()).append("] ");
            result.append(instruction.stringRepresentation()).append("\n");
        }
        return result.toString();
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

    private static List<Variable> extractInputVariables(List<Instruction> instructions) {
       return instructions.stream()
                .map(Instruction::getVariable)
                .filter(var -> var.getType() == VariableType.INPUT)
                .sorted(Comparator.comparingLong(Variable::getNumber))
                .toList();
    }

    private static Map<Label, InstructionLocator> extractLabeledInstructions(List<Instruction> instructions) {
        Map<Label, InstructionLocator> result = new HashMap<>();

        /* populate labeledInstructions with instructionData's of instructions
           with nonempty labels*/
        for(int i = 0; i < instructions.size(); i++){
            Instruction instruction = instructions.get(i);
            if(!instruction.getLabel().equals(FixedLabel.EMPTY))
                result.put(
                        instruction.getLabel(), new InstructionLocator(instruction, i)
                );
        }

        return result;
    }

    private List<Label> extractUsedLabels(boolean usesExit) {
        return Stream.concat(
                        labeledInstructions.keySet().stream(),
                        usesExit ? Stream.of(FixedLabel.EXIT) : Stream.empty()
                )
                .distinct()
                .sorted(Label.comparator())
                .toList();
    }
}