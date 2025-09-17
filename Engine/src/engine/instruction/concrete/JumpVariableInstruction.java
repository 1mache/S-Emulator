package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractJumpInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;
import engine.program.StandardProgram;
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
    protected Program getSyntheticExpansion() {
        int avaliableWorkVarNumber = getAvaliableWorkVarNumber();
        int avaliableLabelNumber = getAvaliableLabelNumber();

        Variable z1 = Variable.createWorkVariable(avaliableWorkVarNumber++);
        Variable z2 = Variable.createWorkVariable(avaliableWorkVarNumber++);
        Label l1 = new NumericLabel(avaliableLabelNumber++);
        Label l2 = new NumericLabel(avaliableLabelNumber++);
        Label l3 = new NumericLabel(avaliableLabelNumber++);
        Label empty = FixedLabel.EMPTY;

        return new StandardProgram(
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
