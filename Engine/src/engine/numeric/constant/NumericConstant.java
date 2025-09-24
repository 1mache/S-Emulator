package engine.numeric.constant;

import engine.execution.context.VariableContext;
import engine.function.parameter.FunctionParam;
import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;

public record NumericConstant(Long value) implements InstructionArgument, FunctionParam {
    @Override
    public InstructionArgumentType getArgumentType() {
        return InstructionArgumentType.CONSTANT;
    }

    @Override
    public Long eval(VariableContext context) {
        return value;
    }
}
