package engine.label;

import engine.argument.Argument;

public interface Label {
    String stringRepresentation();
    Argument toArgument();
}
