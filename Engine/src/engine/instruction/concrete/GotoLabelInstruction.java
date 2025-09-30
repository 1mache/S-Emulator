package engine.instruction.concrete;

import engine.instruction.argument.InstructionArgument;
import engine.execution.context.RunContext;
import engine.instruction.InstructionData;
import engine.instruction.AbstractJumpInstruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.variable.Variable;

import java.util.List;


public class GotoLabelInstruction extends AbstractJumpInstruction {

    public GotoLabelInstruction(
            Label label,
            Label tagetLabel
    ) {
        super(InstructionData.GOTO_LABEL, Variable.NO_VAR, label, tagetLabel);
    }

    @Override
    protected IsJumpResult isJump(RunContext context) {
        return new IsJumpResult(true, staticCycles());
    }

    @Override
    public String stringRepresentation() {
        return "GOTO " + getTargetLabel().stringRepresentation();
    }

    @Override
    public List<InstructionArgument> getArguments() {
        return List.of(getTargetLabel());
    }

    @Override
    protected Program getSyntheticExpansion() {
        Variable z1 = Variable.createWorkVariable(getAvaliableWorkVarNumber());

        return new StandardProgram(
                getName() + "Expansion",
                List.of(
                        new IncreaseInstruction(z1, getLabel()),
                        new JumpNotZeroInstruction(z1, FixedLabel.EMPTY, getTargetLabel())
                )
        );
    }
}
