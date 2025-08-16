package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;
import engine.variable.Variable;

public class NeutralInstruction extends AbstractInstruction {

    public NeutralInstruction(Variable variable) {
        super(InstructionData.NEUTRAL, variable);
    }
    public NeutralInstruction(Variable variable, Label label) {
        super(InstructionData.NEUTRAL, variable, label);
    }

    @Override
    public void execute() {
        return;
    }
}
