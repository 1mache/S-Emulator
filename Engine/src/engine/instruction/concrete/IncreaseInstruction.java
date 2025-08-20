package engine.instruction.concrete;

import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

public class IncreaseInstruction extends AbstractInstruction {

    public IncreaseInstruction(Variable variable) {
        super(InstructionData.INCREASE, variable);
    }
    public IncreaseInstruction(Variable variable, Label label/*variable*/) {
        super(InstructionData.INCREASE, variable, label);
    }

    @Override
    public Label execute(VariableContext context) {
        var curValue = context.getVariableValue(getVariable());
        context.setVariableValue(getVariable(), curValue+1);
        return FixedLabel.EMPTY;
    }
}
