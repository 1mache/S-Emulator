package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;

public class IncreaseInstruction extends AbstractInstruction {

    public IncreaseInstruction(Label label/*variable*/) {
        super(InstructionData.INCREASE, label);
    }

    @Override
    public void execute() {
        System.out.println("Hi twin. My name is IncreaseInstruction");
    }
}
