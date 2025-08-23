package engine.instruction.concrete;

import engine.argument.ConstantArgument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractJumpInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;
import engine.variable.Variable;

public class JumpConstantInstruction extends AbstractJumpInstruction {
    private final ConstantArgument constant;

    public JumpConstantInstruction(Variable variable, Label label, Label targetLabel, ConstantArgument constant) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variable, label, targetLabel);
        this.constant = constant;
    }

    @Override
    protected boolean isJump(VariableContext context) {
        return context.getVariableValue(getVariable()).equals(constant.value());
    }

    @Override
    public String stringRepresentation() {
        return "IF " + getVariable().stringRepresentation() + " = " + constant.value()
                + " GOTO " + getTargetLabel().stringRepresentation();
    }
}
