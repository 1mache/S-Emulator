package engine.function.parameter;

import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;

import java.util.List;
import java.util.stream.Collectors;

public record FunctionParamList(List<FunctionParam> params) implements InstructionArgument {

    @Override
    public InstructionArgumentType getArgumentType() {
        return InstructionArgumentType.FUNC_PARAM_LIST;
    }

    public String stringRepresentation() {
        return params.stream()
                .map(FunctionParam::stringRepresentation)
                .collect(Collectors.joining(", "));
    }
}
