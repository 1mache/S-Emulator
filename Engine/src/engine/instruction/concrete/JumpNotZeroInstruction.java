package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.InstructionData;
import engine.instruction.AbstractJumpInstruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.InstructionLocator;
import engine.variable.Variable;

import java.util.List;

public class JumpNotZeroInstruction extends AbstractJumpInstruction {
    public JumpNotZeroInstruction(Variable variable, Label label, Label targetLabel) {
        this(variable, label, targetLabel, null);
    }

    public JumpNotZeroInstruction(
              Variable variable,
              Label label,
              Label tagetLabel,
              InstructionLocator expanding
    ) {
        super(InstructionData.JUMP_NOT_ZERO, variable, label, tagetLabel, expanding);
    }

    @Override
    protected boolean isJump(VariableContext context) {
        return context.getVariableValue(getVariable()) != 0;
    }

    @Override
    public Label execute(VariableContext context) {
        if(context.getVariableValue(getVariable()) > 0)
            return getTargetLabel();

        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        String varStr = getVariable().stringRepresentation();
        return "IF "+ varStr + "!=0 GOTO " + getTargetLabel().stringRepresentation();
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(getTargetLabel());
    }
}