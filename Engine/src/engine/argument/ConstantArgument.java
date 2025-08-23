package engine.argument;

public record ConstantArgument(Long value) implements Argument {
    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.CONSTANT;
    }
}
