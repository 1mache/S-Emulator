package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;
import engine.variable.Variable;

public class DecreaseInstruction extends AbstractInstruction {

    public DecreaseInstruction(Variable variable) {
        super(InstructionData.DECREASE, variable);
    }
    public DecreaseInstruction(Variable variable, Label label/*variable*/) {
        super(InstructionData.DECREASE, variable, label);
    }

    @Override
    public void execute() {
        return;
    }
}
