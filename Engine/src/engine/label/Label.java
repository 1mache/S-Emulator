package engine.label;

import engine.argument.Argument;
import engine.argument.ArgumentType;

public interface Label extends Argument {
    String stringRepresentation();

    @Override
    default ArgumentType getArgumentType() {
        return ArgumentType.LABEL;
    }
}
