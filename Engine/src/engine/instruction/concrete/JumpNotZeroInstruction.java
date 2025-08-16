package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;

public class JumpNotZeroInstruction extends AbstractInstruction {
    public JumpNotZeroInstruction(Label label) {
        super(InstructionData.JUMP_NOT_ZERO, label);
    }

    @Override
    public void execute() {
        return;
    }
}
