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
    public Long eval(RunContext context) {
        return value;
    }

    @Override
    public String stringRepresentation() {
        return value.toString();
    }
}
