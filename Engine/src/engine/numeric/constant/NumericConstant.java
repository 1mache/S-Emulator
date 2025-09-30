package engine.numeric.constant;

import engine.execution.context.RunContext;
import engine.function.parameter.FunctionParam;
import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;

public record NumericConstant(Long value) implements InstructionArgument, FunctionParam {
    @Override
    public InstructionArgumentType getArgumentType() {
        return InstructionArgumentType.CONSTANT;
    }

    @Override
    public EvaluationResult eval(RunContext context) {
        return new EvaluationResult(value, 0);
    }

    @Override
    public String stringRepresentation() {
        return value.toString();
    }
}
