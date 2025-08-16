package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;

public class JumpNotZeroInstruction extends AbstractInstruction {
    public JumpNotZeroInstruction() {
        super(InstructionData.JUMP_NOT_ZERO);
    }

    @Override
    public void execute() {
        return;
    }
}
