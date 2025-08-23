package engine.instruction.concrete;

import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

public class JumpNotZeroInstruction extends AbstractInstruction {
    private final Label targetLabel;

    public JumpNotZeroInstruction(Variable variable, Label label, Label targetLabel) {
        super(InstructionData.JUMP_NOT_ZERO, variable, label);
        this.targetLabel = targetLabel;
    }

    @Override
    public Label execute(VariableContext context) {
        if(context.getVariableValue(getVariable()) > 0)
            return targetLabel;

        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        String varStr = getVariable().stringRepresentation();
        return "IF "+ varStr + "!=0 GOTO " + targetLabel.stringRepresentation();
    }
}