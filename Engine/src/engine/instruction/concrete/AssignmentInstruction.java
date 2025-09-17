package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.variable.Variable;

import java.util.List;

public class AssignmentInstruction extends AbstractInstruction {
    private final Variable assignedVariable;

    public AssignmentInstruction(
            Variable variable,
            Label label,
            Variable assignedVariable
    ) {
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

    @Override
    public List<Argument> getArguments() {
        return List.of(assignedVariable);
    }

    @Override
    protected Program getSyntheticExpansion() {
        int avaliableLabelNumber = getAvaliableLabelNumber();

        Label l1 = new NumericLabel(avaliableLabelNumber++);
        Label l2 = new NumericLabel(avaliableLabelNumber++);
        Label l3 = new NumericLabel(avaliableLabelNumber++);
        Label empty = FixedLabel.EMPTY;
        Variable z1 = Variable.createWorkVariable(getAvaliableWorkVarNumber());

        return new StandardProgram(
                getName() + "Expansion",
                List.of(
                        new ZeroVariableInstruction(getVariable(), getLabel()),
                        new JumpNotZeroInstruction(assignedVariable, empty, l1),
                        new GotoLabelInstruction(empty, l3),
                        new DecreaseInstruction(assignedVariable, l1),
                        new IncreaseInstruction(z1, empty),
                        new JumpNotZeroInstruction(assignedVariable, empty, l1),
                        new DecreaseInstruction(z1, l2),
                        new IncreaseInstruction(getVariable(), empty),
                        new IncreaseInstruction(assignedVariable, empty),
                        new JumpNotZeroInstruction(z1, empty, l2),
                        new NeutralInstruction(getVariable(), l3)
                )
        );
    }
}
