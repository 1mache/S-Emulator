package engine.instruction.concrete;

import engine.execution.context.VariableContext;
import engine.instruction.AbstractJumpInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;
import engine.variable.Variable;

public class JumpVariableInstruction extends AbstractJumpInstruction {
    private final Variable otherVariable;
    public JumpVariableInstruction(Variable variable, Label label, Label targetLabel, Variable otherVariable) {
        super(InstructionData.JUMP_EQUAL_VARIABLE, variable, label, targetLabel);
        this.otherVariable = otherVariable;
    }

    @Override
    protected boolean isJump(VariableContext context) {
        return context.getVariableValue(getVariable())
                .equals(context.getVariableValue(otherVariable));
    }

    @Override
    public String stringRepresentation() {
        return "IF " + getTargetLabel().stringRepresentation() +
                " = " + otherVariable.stringRepresentation() +
                " GOTO " + getTargetLabel().stringRepresentation();
    }
}
