package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;

public class NeutralInstruction extends AbstractInstruction {
    public NeutralInstruction(Label label) {
        super(InstructionData.NEUTRAL, label);
    }

    @Override
    public void execute() {
        return;
    }
}
