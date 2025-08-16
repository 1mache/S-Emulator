package engine.instruction;

import engine.instruction.concrete.*;

public abstract class AbstractInstruction implements Instruction {
    private final InstructionData data;

    protected AbstractInstruction(InstructionData data) {
        this.data = data;
    }

    public static Instruction createInstruction(InstructionData data) {
        return switch (data) {
            case INCREASE -> new IncreaseInstruction();
            case DECREASE -> new DecreaseInstruction();
            case JUMP_NOT_ZERO -> new JumpNotZeroInstruction();
            case NEUTRAL -> new Neutralnstruction();
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
}
