package engine.instruction.concrete;

import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

public class DecreaseInstruction extends AbstractInstruction {

    public DecreaseInstruction(Variable variable, Label label) {
        super(InstructionData.DECREASE, variable, label);
    }

    @Override
    public Label execute(VariableContext context) {
        var curValue = context.getVariableValue(getVariable());
        if(curValue>0) // only decreases to a minimum of 0
            context.setVariableValue(getVariable(), curValue-1);
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        String varStr = getVariable().stringRepresentation();
        return varStr + " <- " + varStr + " - 1";
    }
}
