package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.InstructionLocator;
import engine.variable.Variable;

import java.util.List;

public class DecreaseInstruction extends AbstractInstruction {

    public DecreaseInstruction(Variable variable, Label label) {
        this(variable, label, null);
    }

    public DecreaseInstruction(Variable variable, Label label, InstructionLocator expanding) {
        super(InstructionData.DECREASE, variable, label, expanding);
    }

    @Override
    public Label execute(VariableContext context) {
        var curValue = context.getVariableValue(getVariable());
        if(curValue>0) // only decreases to a minimum of 0
            context.setVariableValue(getVariable(), curValue-1);
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        String varStr = getVariable().stringRepresentation();
        return varStr + " <- " + varStr + " - 1";
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(); // no arguments
    }
}
