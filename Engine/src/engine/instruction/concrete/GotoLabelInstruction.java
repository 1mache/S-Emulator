package engine.instruction.concrete;

import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;
import engine.variable.Variable;


public class GotoLabelInstruction extends AbstractInstruction {
    private final Label targetLabel;

    public GotoLabelInstruction(Label label, Label targetLabel) {
        super(InstructionData.GOTO_LABEL, Variable.NONE, label);
        this.targetLabel = targetLabel;
    }

    @Override
    public Label execute(VariableContext context) {
        return targetLabel;
    }

    @Override
    public String stringRepresentation() {
        return "GOTO " + getLabel().stringRepresentation();
    }
}
