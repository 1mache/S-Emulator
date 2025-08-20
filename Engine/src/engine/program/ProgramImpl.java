package engine.program;

import engine.instruction.Instruction;
import engine.instruction.InstructionIdentifier;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.*;

public class ProgramImpl implements Program {
    private final Map<Label, InstructionIdentifier> labeledInstructions = new HashMap<>();
    private final List<Instruction> instructions;

    private final List<Variable> inputVariables = new ArrayList<>();

    private final String name;

    public ProgramImpl(String name, List<Instruction> instructions) {
        this.name = name;
        this.instructions = instructions;

        /* populate labeledInstructions with instructionData's of instructions
           with nonempty labels*/
        for(int i = 0; i < instructions.size(); i++){
            Instruction instruction = instructions.get(i);
            if(!instruction.getLabel().equals(FixedLabel.EMPTY))
                labeledInstructions.put(
                        instruction.getLabel(), new InstructionIdentifier(instruction, i)
                );
        }
        /* populate the inputVariables list with input variables used in the
           instruction */
        instructions.forEach(instruction -> {
            if(instruction.getVariable().getType() == VariableType.INPUT)
                inputVariables.add(instruction.getVariable());
        });
        // sort input
        inputVariables.sort(Comparator.comparingLong(Variable::getNumber));
    };

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Variable> getInputVariables() {
        return inputVariables;
    }

    @Override
    public boolean hasLabel(Label label) {
        return labeledInstructions.containsKey(label);
    }

    @Override
    public Optional<Instruction> getInstruction(Label label) {
        return Optional.ofNullable(labeledInstructions.get(label).instruction());
    }

    @Override
    public Optional<Integer> getLabelLine(Label label) {
        if(label == FixedLabel.EMPTY)
            return Optional.empty();
        return Optional.of(labeledInstructions.get(label).pcId());
    }

    @Override
    public Optional<Instruction> getInstructionByIndex(int index) {
        if(index >= instructions.size())
            return Optional.empty();
        return Optional.of(instructions.get(index));
    }
}