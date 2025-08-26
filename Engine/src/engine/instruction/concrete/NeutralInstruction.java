package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.InstructionReference;
import engine.variable.Variable;

import java.util.List;

public class NeutralInstruction extends AbstractInstruction {

    public NeutralInstruction(Variable variable, Label label) {
        this(variable, label, null);
    }

    public NeutralInstruction(
              Variable variable,
              Label label,
              InstructionReference expanding
    ) {
        super(InstructionData.NEUTRAL, variable, label, expanding);
    }

    @Override
    public Label execute(VariableContext context){
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        String varStr = getVariable().stringRepresentation();
        return varStr + " <- " + varStr;
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(); // no arguments
    }
}
