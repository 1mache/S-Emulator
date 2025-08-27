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

public class JumpZeroInstruction extends AbstractJumpInstruction {
    public JumpZeroInstruction(Variable variable, Label label, Label targetLabel) {
        this(variable, label, targetLabel, null);
    }

    public JumpZeroInstruction(
           Variable variable,
           Label label,
           Label tagetLabel,
           InstructionReference expanding
    ) {
        super(InstructionData.JUMP_ZERO, variable, label, tagetLabel, expanding);
    }

    @Override
    protected boolean isJump(VariableContext context) {
        return context.getVariableValue(getVariable()) == 0;
    }

    @Override
    public String stringRepresentation() {
        return "IF " + getVariable().stringRepresentation() + " = 0 GOTO "
                + getTargetLabel().stringRepresentation();
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(getTargetLabel());
    }

    @Override
    protected Program getSyntheticExpansion(int lineNumber, LabelVariableGenerator generator) {
        InstructionReference locator = new InstructionReference(this, lineNumber);
        Label l1 = generator.getNextLabel();

        return new ProgramImpl(
                getName() + "Expansion",
                List.of(
                        new JumpNotZeroInstruction(getVariable(), getLabel(), l1, locator),
                        new GotoLabelInstruction(FixedLabel.EMPTY, getTargetLabel() , locator),
                        new NeutralInstruction(Variable.RESULT, l1, locator)
                )
        );
    }
}
