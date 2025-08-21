package engine.program;

import engine.instruction.Instruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.*;

public class ProgramImpl implements Program {

    // record that represents instruction and its ordinal id in the program
    private record InstructionLocator(Instruction instruction, int lineId) { }

    private final Map<Label, InstructionLocator> labeledInstructions = new HashMap<>();
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
                        instruction.getLabel(), new InstructionLocator(instruction, i)
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