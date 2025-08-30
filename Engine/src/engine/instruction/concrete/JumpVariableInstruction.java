package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractJumpInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.Program;
import engine.program.ProgramImpl;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.List;

public class JumpVariableInstruction extends AbstractJumpInstruction {
    private final Variable otherVariable;

    public JumpVariableInstruction(
           Variable variable,
           Label label,
           Label tagetLabel,
           Variable otherVariable
    ) {
        super(InstructionData.JUMP_EQUAL_VARIABLE, variable, label, tagetLabel);
        this.otherVariable = otherVariable;
    }

    @Override
    protected boolean isJump(VariableContext context) {
        return context.getVariableValue(getVariable())
                .equals(context.getVariableValue(otherVariable));
    }

    @Override
    public String stringRepresentation() {
        return "IF " + getVariable().stringRepresentation() +
                " = " + otherVariable.stringRepresentation() +
                " GOTO " + getTargetLabel().stringRepresentation();
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(getTargetLabel(), otherVariable);
    }

    @Override
    protected Program getSyntheticExpansion(LabelVariableGenerator generator) {
        Variable z1 = generator.getNextWorkVariable();
        Variable z2 = generator.getNextWorkVariable();
        Label l1 = generator.getNextLabel();
        Label l2 = generator.getNextLabel();
        Label l3 = generator.getNextLabel();
        Label empty = FixedLabel.EMPTY;

        return new ProgramImpl(
                getName() + "Expansion",
                List.of(
                        new AssignmentInstruction(z1, getLabel(), getVariable()),
                        new AssignmentInstruction(z2, empty, otherVariable),
                        new JumpZeroInstruction(z1, l2, l3),
                        new JumpZeroInstruction(z2, empty, l1),
                        new DecreaseInstruction(z1, empty),
                        new DecreaseInstruction(z2, empty),
                        new GotoLabelInstruction(empty, l2),
                        new JumpZeroInstruction(z2, l3, getTargetLabel()),
                        new NeutralInstruction(Variable.RESULT, l1)
                )
        );
    }
}
