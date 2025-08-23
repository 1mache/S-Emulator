package engine.instruction.concrete;

import engine.execution.context.VariableContext;
import engine.instruction.InstructionData;
import engine.instruction.AbstractJumpInstruction;
import engine.label.Label;
import engine.variable.Variable;

public class JumpZeroInstruction extends AbstractJumpInstruction {
    public JumpZeroInstruction(Variable variable, Label label, Label targetLabel) {
        super(InstructionData.JUMP_ZERO, variable, label, targetLabel);
    }

    @Override
    protected boolean isJump(VariableContext context) {
        return context.getVariableValue(getVariable()) == 0;
    }

    @Override
    public String stringRepresentation() {
        return "";
    }
}
