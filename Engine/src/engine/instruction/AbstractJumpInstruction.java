package engine.instruction;

import engine.execution.context.VariableContext;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

public abstract class AbstractJumpInstruction extends AbstractInstruction {
    private final Label tagetLabel;

    public AbstractJumpInstruction(
           InstructionData data,
           Variable variable,
           Label label,
           Label tagetLabel
    ) {
        super(data, variable, label);
        this.tagetLabel = tagetLabel;
    }

    /**
     * @return condition on which we jump
     */
    protected abstract boolean isJump(VariableContext context);

    @Override
    public Label execute(VariableContext context) {
        if(isJump(context))
            return tagetLabel;

        return FixedLabel.EMPTY;
    }

    protected Label getTargetLabel(){
        return tagetLabel;
    }
}
