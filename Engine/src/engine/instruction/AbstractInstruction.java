package engine.instruction;

import engine.instruction.concrete.*;
import engine.label.FixedLabel;
import engine.label.Label;

public abstract class AbstractInstruction implements Instruction {
    private final InstructionData data;
    private final Label label;

    protected AbstractInstruction(InstructionData data) {
        this(data, FixedLabel.EMPTY);
    }
    protected AbstractInstruction(InstructionData data, Label label) {
        this.data = data;
        this.label = label;
    }

    // factory method based on InstructionData
    public static Instruction createInstruction(InstructionData data, Label label) {
        return switch (data) {
            case INCREASE -> new IncreaseInstruction(label);
            case DECREASE -> new DecreaseInstruction(label);
            case JUMP_NOT_ZERO -> new JumpNotZeroInstruction(label);
            case NEUTRAL -> new NeutralInstruction(label);
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
    public Label getLabel() {
        return label;
    }
}
