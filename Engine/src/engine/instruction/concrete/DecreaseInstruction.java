package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;

public class DecreaseInstruction extends AbstractInstruction {

    public DecreaseInstruction(Label label/*variable*/) {
        super(InstructionData.DECREASE, label);
    }

    @Override
    public void execute() {
        return;
    }
}
