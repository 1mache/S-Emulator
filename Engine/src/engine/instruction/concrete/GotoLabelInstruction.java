package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.InstructionData;
import engine.instruction.AbstractJumpInstruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.program.generator.LabelVariableGenerator;
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
    protected boolean isJump(VariableContext context) {
        return true;
    }

    @Override
    public Label execute(VariableContext context) {
        return getTargetLabel();
    }

    @Override
    public String stringRepresentation() {
        return "GOTO " + getTargetLabel().stringRepresentation();
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(getTargetLabel());
    }

    @Override
    protected Program getSyntheticExpansion(LabelVariableGenerator generator) {
        Variable z1 = generator.getNextWorkVariable();

        return new StandardProgram(
                getName() + "Expansion",
                List.of(
                        new IncreaseInstruction(z1, getLabel()),
                        new JumpNotZeroInstruction(z1, FixedLabel.EMPTY, getTargetLabel())
                )
        );
    }
}
