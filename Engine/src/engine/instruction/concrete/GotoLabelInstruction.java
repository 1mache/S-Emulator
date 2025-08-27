package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.InstructionData;
import engine.instruction.AbstractJumpInstruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.InstructionReference;
import engine.program.Program;
import engine.program.ProgramImpl;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.List;


public class GotoLabelInstruction extends AbstractJumpInstruction {

    public GotoLabelInstruction(Label label, Label targetLabel) {
        this(label, targetLabel, null);
    }

    public GotoLabelInstruction(
            Label label,
            Label tagetLabel,
            InstructionReference expanding
    ) {
        super(InstructionData.GOTO_LABEL, Variable.NO_VAR, label, tagetLabel, expanding);
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
    protected Program getSyntheticExpansion(int lineNumber, LabelVariableGenerator generator) {
        InstructionReference locator = new InstructionReference(this, lineNumber);
        Variable z1 = generator.getNextWorkVariable();

        return new ProgramImpl(
                getName() + "Expansion",
                List.of(
                        new IncreaseInstruction(z1, getLabel(), locator),
                        new JumpNotZeroInstruction(z1, FixedLabel.EMPTY, getTargetLabel(), locator)
                )
        );
    }
}
