package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;
import engine.variable.Variable;

public class JumpNotZeroInstruction extends AbstractInstruction {

    public JumpNotZeroInstruction(Variable variable) {
        super(InstructionData.JUMP_NOT_ZERO, variable);
    }
    public JumpNotZeroInstruction(Variable variable, Label label) {
        super(InstructionData.JUMP_NOT_ZERO, variable, label);
    }

    @Override
    public void execute() {
        return;
    }
}
