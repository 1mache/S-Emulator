package engine.instruction.concrete;

import engine.execution.InstructionExecutionResult;
import engine.instruction.argument.InstructionArgument;
import engine.execution.context.RunContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

import java.util.List;

public class DecreaseInstruction extends AbstractInstruction {

    public DecreaseInstruction(Variable variable, Label label) {
        super(InstructionData.DECREASE, variable, label);
    }

    @Override
    public InstructionExecutionResult execute(RunContext context) {
        var curValue = context.getVariableValue(getVariable());
        if(curValue>0) // only decreases to a minimum of 0
            context.setVariableValue(getVariable(), curValue-1);
        return new InstructionExecutionResult(FixedLabel.EMPTY, staticCycles());
    }

    @Override
    public String stringRepresentation() {
        String varStr = getVariable().stringRepresentation();
        return varStr + " <- " + varStr + " - 1";
    }

    @Override
    public List<InstructionArgument> getArguments() {
        return List.of(); // no arguments
    }
}
