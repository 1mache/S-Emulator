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

public class ZeroVariableInstruction extends AbstractInstruction {

    public ZeroVariableInstruction(Variable variable, Label label) {
        this(variable, label, null);
    }

    public ZeroVariableInstruction(
           Variable variable,
           Label label,
           InstructionLocator expanding
    ) {
        super(InstructionData.ZERO_VARIABLE, variable, label, expanding);
    }

    @Override
    public Label execute(VariableContext context) {
        context.setVariableValue(getVariable(), 0);
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        return getVariable().stringRepresentation() + " <- 0";
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(); // no arguments
    }
}