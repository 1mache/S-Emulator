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
    private final List<Argument> arguments;
    private Label targetLabel;
    private static final int ARG_AMOUNT = 1;

    public JumpNotZeroInstruction(Variable variable, List<Argument> arguments) {
        super(InstructionData.JUMP_NOT_ZERO, variable, arguments);
        this.arguments = arguments;
    }
    public JumpNotZeroInstruction(Variable variable, Label label, List<Argument> arguments) {
        super(InstructionData.JUMP_NOT_ZERO, variable, arguments);
        this.arguments = arguments;
    }

    @Override
    public Label execute(VariableContext context) {
        if(context.getVariableValue(getVariable()) > 0)
            return targetLabel;

        return FixedLabel.EMPTY;
    }

    @Override
    public boolean processArguments() {
        if(arguments.size() != ARG_AMOUNT)
            return false;

        if(!(arguments.getFirst() instanceof Label))
            return false; // needs to be a label

        targetLabel = (Label) arguments.getFirst();
        return true;
    }
}
