package engine.instruction.concrete;

import engine.instruction.argument.InstructionArgument;
import engine.execution.context.RunContext;
import engine.instruction.InstructionData;
import engine.instruction.AbstractJumpInstruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.variable.Variable;

import java.util.List;

public class JumpZeroInstruction extends AbstractJumpInstruction {

    public JumpZeroInstruction(
           Variable variable,
           Label label,
           Label tagetLabel
    ) {
        super(InstructionData.JUMP_ZERO, variable, label, tagetLabel);
    }

    @Override
    protected IsJumpResult isJump(RunContext context) {
        return new IsJumpResult(context.getVariableValue(getVariable()) == 0, staticCycles());
    }

    @Override
    public String stringRepresentation() {
        return "IF " + getVariable().stringRepresentation() + " = 0 GOTO "
                + getTargetLabel().stringRepresentation();
    }

    @Override
    public List<InstructionArgument> getArguments() {
        return List.of(getTargetLabel());
    }

    @Override
    protected Program getSyntheticExpansion() {
        Label l1 = new NumericLabel(getAvaliableLabelNumber());

        return new StandardProgram(
                getName() + "Expansion",
                List.of(
                        new JumpNotZeroInstruction(getVariable(), getLabel(), l1),
                        new GotoLabelInstruction(FixedLabel.EMPTY, getTargetLabel()),
                        new NeutralInstruction(Variable.RESULT, l1)
                )
        );
    }
}
