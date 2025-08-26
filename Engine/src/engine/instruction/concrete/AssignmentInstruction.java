package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.InstructionReference;
import engine.program.Program;
import engine.program.ProgramImpl;
import engine.variable.Variable;

import java.util.List;

public class AssignmentInstruction extends AbstractInstruction {
    private final Variable assignedVariable;

    public AssignmentInstruction(Variable variable, Label label, Variable assignedVariable) {
        this(variable, label, assignedVariable, null);
    }

    public AssignmentInstruction(
            Variable variable,
            Label label,
            Variable assignedVariable,
            InstructionReference expanding
    ) {
        super(InstructionData.ASSIGNMENT, variable, label, expanding);
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
        return List.of(assignedVariable); // no arguments
    }

    @Override
    protected Program getSyntheticExpansion(int lineNumber) {
        InstructionReference locator = new InstructionReference(this, lineNumber);
        Label l1 = new NumericLabel(1);
        Label l2 = new NumericLabel(2);
        Label l3 = new NumericLabel(3);
        Label empty = FixedLabel.EMPTY;
        Variable z1 = Variable.createWorkVariable(1);
        Variable z2 = Variable.createWorkVariable(2);

        return new ProgramImpl(
                getName() + "Expansion",
                List.of(
                        new ZeroVariableInstruction(getVariable(), empty, locator),
                        new JumpNotZeroInstruction(assignedVariable, empty, l1, locator),
                        new GotoLabelInstruction(z2,empty, l3, locator),
                        new DecreaseInstruction(assignedVariable, l1, locator),
                        new IncreaseInstruction(z1, empty, locator),
                        new JumpNotZeroInstruction(assignedVariable, empty, l1, locator),
                        new DecreaseInstruction(z1, l2, locator),
                        new IncreaseInstruction(getVariable(), empty, locator),
                        new IncreaseInstruction(assignedVariable, empty, locator),
                        new JumpNotZeroInstruction(z1, empty, l2, locator),
                        new NeutralInstruction(getVariable(), l3, locator)
                )
        );
    }
}
