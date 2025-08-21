package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

import java.util.List;

public class JumpNotZeroInstruction extends AbstractInstruction {
    private final Label targetLabel;

    public JumpNotZeroInstruction(Variable variable, List<Argument> arguments) {
        super(InstructionData.JUMP_NOT_ZERO, variable, arguments);
        targetLabel = (Label) arguments.getFirst();
    }
    public JumpNotZeroInstruction(Variable variable, Label label, List<Argument> arguments) {
        super(InstructionData.JUMP_NOT_ZERO, variable, label, arguments);
        targetLabel = (Label) arguments.getFirst();
    }

    @Override
    public Label execute(VariableContext context) {
        if(context.getVariableValue(getVariable()) > 0)
            return targetLabel;

        return FixedLabel.EMPTY;
    }
}
