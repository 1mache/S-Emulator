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

public class IncreaseInstruction extends AbstractInstruction {

    public IncreaseInstruction(Variable variable, Label label) {
        super(InstructionData.INCREASE, variable, label);
    }

    @Override
    public InstructionExecutionResult execute(RunContext context) {
        var curValue = context.getVariableValue(getVariable());
        context.setVariableValue(getVariable(), curValue+1);
        return new InstructionExecutionResult(FixedLabel.EMPTY, staticCycles());
    }

    @Override
    public String stringRepresentation() {
        String varStr = getVariable().stringRepresentation();
        return varStr + " <- " + varStr + " + 1";
    }

    @Override
    public List<InstructionArgument> getArguments() {
        return List.of(); // no arguments
    }
}
