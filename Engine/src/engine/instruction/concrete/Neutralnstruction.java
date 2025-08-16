package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;

public class Neutralnstruction extends AbstractInstruction {
    public Neutralnstruction() {
        super(InstructionData.NEUTRAL);
    }

    @Override
    public void execute() {
        return;
    }
}
