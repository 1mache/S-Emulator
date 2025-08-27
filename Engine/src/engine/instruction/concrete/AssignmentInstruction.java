package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.InstructionReference;
import engine.program.Program;
import engine.program.ProgramImpl;
import engine.program.generator.LabelVariableGenerator;
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
    protected Program getSyntheticExpansion(int lineNumber, LabelVariableGenerator generator) {
        InstructionReference locator = new InstructionReference(this, lineNumber);
        Label l1 = generator.getNextLabel();
        Label l2 = generator.getNextLabel();
        Label l3 = generator.getNextLabel();
        Label empty = FixedLabel.EMPTY;
        Variable z1 = generator.getNextWorkVariable();

        return new ProgramImpl(
                getName() + "Expansion",
                List.of(
                        new ZeroVariableInstruction(getVariable(), getLabel(), locator),
                        new JumpNotZeroInstruction(assignedVariable, empty, l1, locator),
                        new GotoLabelInstruction(empty, l3, locator),
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
