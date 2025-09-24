package engine.instruction.concrete;

import engine.instruction.argument.InstructionArgument;
import engine.execution.context.RunContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

import java.util.List;

public class NeutralInstruction extends AbstractInstruction {

    public NeutralInstruction(
              Variable variable,
              Label label
    ) {
        super(InstructionData.NEUTRAL, variable, label);
    }

    @Override
    public Label execute(RunContext context){
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        String varStr = getVariable().stringRepresentation();
        return varStr + " <- " + varStr;
    }

    @Override
    public List<InstructionArgument> getArguments() {
        return List.of(); // no arguments
    }
}
