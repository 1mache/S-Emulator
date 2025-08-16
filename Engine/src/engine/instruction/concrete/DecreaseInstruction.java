package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;

public class DecreaseInstruction extends AbstractInstruction {

    public DecreaseInstruction(/*variable*/) {
        super(InstructionData.DECREASE);
    }

    @Override
    public void execute() {
        return;
    }
}
