package engine.instruction.concrete;

import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

public class AssignmentInstruction extends AbstractInstruction {
    private final Variable assignedVariable;

    public AssignmentInstruction(Variable variable, Label label, Variable assignedVariable) {
        super(InstructionData.ASSIGNMENT, variable, label);
        this.assignedVariable = assignedVariable;
    }

    @Override
    public Label execute(VariableContext context) {
        context.setVariableValue(getVariable(), context.getVariableValue(assignedVariable));
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        return getVariable().stringRepresentation() + " <- " + assignedVariable.stringRepresentation();
    }
}
