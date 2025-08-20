package engine.instruction;

import engine.execution.context.VariableContext;
import engine.instruction.concrete.*;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

public abstract class AbstractInstruction implements Instruction {
    private final InstructionData data;
    private final Label label;
    private final Variable variable;


    protected AbstractInstruction(InstructionData data, Variable variable) {
        this(data, variable, FixedLabel.EMPTY);
    }
    protected AbstractInstruction(InstructionData data, Variable variable, Label label) {
        this.data = data;
        this.variable = variable;
        this.label = label;
    }

    // factory method based on InstructionData
    public static Instruction createInstruction(InstructionData data, Variable variable, Label label) {
        return switch (data) {
            case INCREASE -> new IncreaseInstruction(variable,label);
            case DECREASE -> new DecreaseInstruction(variable,label);
            case JUMP_NOT_ZERO -> new JumpNotZeroInstruction(variable,label);
            case NEUTRAL -> new NeutralInstruction(variable,label);
        };
    }

    @Override
    public String getName() {
        return data.name();
    }

    @Override
    public int cycles() {
        return data.getCycles();
    }

    @Override
    public Variable getVariable() {
        return variable;
    }

    @Override
    public Label getLabel() {
        return label;
    }
}
