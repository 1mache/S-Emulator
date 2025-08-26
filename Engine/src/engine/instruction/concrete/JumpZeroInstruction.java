package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.InstructionData;
import engine.instruction.AbstractJumpInstruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.InstructionLocator;
import engine.program.Program;
import engine.program.ProgramImpl;
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
           InstructionLocator expanding
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
    protected Program getSyntheticExpansion(int lineNumber) {
        InstructionLocator locator = new InstructionLocator(this, lineNumber);
        Variable z1 = Variable.createWorkVariable(1);
        Label l1 = new NumericLabel(1);

        return new ProgramImpl(
                getName() + "Expansion",
                List.of(
                        new JumpNotZeroInstruction(getVariable(), FixedLabel.EMPTY, l1, locator),
                        new GotoLabelInstruction(z1, FixedLabel.EMPTY, getTargetLabel() , locator),
                        new NeutralInstruction(Variable.RESULT, l1, locator)
                )
        );
    }
}
