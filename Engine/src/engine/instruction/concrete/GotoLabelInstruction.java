package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.InstructionData;
import engine.instruction.AbstractJumpInstruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.InstructionLocator;
import engine.program.Program;
import engine.program.ProgramImpl;
import engine.variable.Variable;

import java.util.List;


public class GotoLabelInstruction extends AbstractJumpInstruction {

    public GotoLabelInstruction(Variable variable, Label label, Label targetLabel) {
        this(variable, label, targetLabel, null);
    }

    public GotoLabelInstruction(
            Variable variable,
            Label label,
            Label tagetLabel,
            InstructionLocator expanding
    ) {
        super(InstructionData.GOTO_LABEL, variable, label, tagetLabel, expanding);
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
    protected Program getSyntheticExpansion(int lineNumber) {
        InstructionLocator locator = new InstructionLocator(this, lineNumber);
        return new ProgramImpl(
                getName() + "Expansion",
                List.of(
                        new IncreaseInstruction(getVariable(), FixedLabel.EMPTY, locator),
                        new JumpNotZeroInstruction(getVariable(), FixedLabel.EMPTY, getTargetLabel(), locator)
                )
        );
    }
}
