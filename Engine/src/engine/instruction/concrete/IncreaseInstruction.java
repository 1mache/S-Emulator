package engine.instruction.concrete;

import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;

public class IncreaseInstruction extends AbstractInstruction {

    public IncreaseInstruction(/*variable*/) {
        super(InstructionData.INCREASE);
    }

    @Override
    public void execute() {
        System.out.println("Hi twin. My name is IncreaseInstruction");
    }
}
