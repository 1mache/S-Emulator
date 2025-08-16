package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
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
    public void execute() {
        System.out.println("Hi twin. My name is IncreaseInstruction");
    }
}
