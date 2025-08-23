package engine.instruction.concrete;

import engine.execution.context.VariableContext;
import engine.instruction.InstructionData;
import engine.instruction.AbstractJumpInstruction;
import engine.label.Label;
import engine.variable.Variable;


public class GotoLabelInstruction extends AbstractJumpInstruction {
    private final Label targetLabel;

    public GotoLabelInstruction(Label label, Label targetLabel) {
        super(InstructionData.GOTO_LABEL, Variable.NONE, label,  targetLabel);
        this.targetLabel = targetLabel;
    }

    @Override
    protected boolean isJump(VariableContext context) {
        return true;
    }

    @Override
    public Label execute(VariableContext context) {
        return targetLabel;
    }

    @Override
    public String stringRepresentation() {
        return "GOTO " + targetLabel.stringRepresentation();
    }
}
