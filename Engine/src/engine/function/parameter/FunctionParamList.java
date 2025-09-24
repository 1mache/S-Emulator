package engine.function.parameter;

import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;

import java.util.List;

public record FunctionParamList(List<FunctionParam> params) implements InstructionArgument {

    @Override
    public InstructionArgumentType getArgumentType() {
        return InstructionArgumentType.FUNC_ARG_LIST;
    }
}
