package engine.program;

import engine.instruction.Instruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

import java.util.*;

public class ProgramImpl implements Program {
    private final Map<Variable, Integer> variableTable = new HashMap<>();

    // record that represents instruction and its ordinal id in the program
    private record InstructionData(Instruction instruction, int pcId) {}
    private final Map<Label, InstructionData> labeledInstructions = new HashMap<>();
    private final List<Instruction> instructions;

    private final String name;

    private int pc = 0;

    public ProgramImpl(String name, List<Instruction> instructions) {
        this.name = name;
        this.instructions = instructions;

        /* populate labeledInstructions with instructionData's of instructions
        with nonempty labels*/
        for(int i = 0; i < instructions.size(); i++){
            Instruction instruction = instructions.get(i);
            if(!instruction.getLabel().equals(FixedLabel.EMPTY))
                labeledInstructions.put(
                        instruction.getLabel(), new InstructionData(instruction, i)
                );
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasLabel(Label label) {
        return labeledInstructions.containsKey(label);
    }

    @Override
    public Optional<Instruction> getInstruction(Label label) {
        return Optional.ofNullable(labeledInstructions.get(label).instruction);
    }

    @Override
    public Optional<Instruction> getNextInstruction() {
        if(pc == instructions.size()) return Optional.empty();
        // should not be null since we checked bounds
        return Optional.of(instructions.get(pc++));
    }

    @Override
    public int getVariableState(Variable variable) {
        Optional<Integer> value = Optional.ofNullable(variableTable.get(variable));
        return value.orElse(0);
    }

    @Override
    public void setVariableState(Variable variable, int value) {
        variableTable.put(variable, value);
    }
}