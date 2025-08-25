package engine.instruction.concrete;

import engine.argument.Argument;
import engine.argument.ConstantArgument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.InstructionLocator;
import engine.variable.Variable;

import java.util.List;

public class ConstantAssignmentInstruction extends AbstractInstruction {
    private final ConstantArgument constant;

    public ConstantAssignmentInstruction(Variable variable, Label label, ConstantArgument constant) {
        this(variable, label, constant, null);
    }

    public ConstantAssignmentInstruction(
             Variable variable,
             Label label,
             ConstantArgument constant,
             InstructionLocator expanding
    ) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variable, label, expanding);
        this.constant = constant;
    }

    @Override
    public Label execute(VariableContext context) {
        context.setVariableValue(getVariable(), constant.value());
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        return getVariable().stringRepresentation() + " <- " + constant.value();
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(constant);
    }
}
