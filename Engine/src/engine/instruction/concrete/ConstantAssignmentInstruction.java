package engine.instruction.concrete;

import engine.argument.ConstantArgument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

public class ConstantAssignmentInstruction extends AbstractInstruction {
    private final ConstantArgument constant;

    public ConstantAssignmentInstruction(Variable variable, Label label, ConstantArgument constant) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variable, label);
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
}
