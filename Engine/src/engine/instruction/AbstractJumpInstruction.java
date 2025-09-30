package engine.instruction;

import engine.execution.InstructionExecutionResult;
import engine.execution.context.RunContext;
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


    // sometimes calculating whether we jump or not is not something that's done in a constant number of cycles
    public record IsJumpResult(boolean jumped, long cyclesCost){}

    /**
     * @return condition on which we jump + how many cycles does the calculation (execution) take
     */
    protected abstract IsJumpResult isJump(RunContext context);

    @Override
    public InstructionExecutionResult execute(RunContext context) {
        var isJumpResult = isJump(context);
        Label resultLabel;

        if(isJumpResult.jumped)
            resultLabel = tagetLabel;
        else resultLabel = FixedLabel.EMPTY;

        return new InstructionExecutionResult(resultLabel, isJumpResult.cyclesCost);
    }

    protected Label getTargetLabel(){
        return tagetLabel;
    }
}
