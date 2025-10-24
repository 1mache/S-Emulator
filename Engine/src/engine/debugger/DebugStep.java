package engine.debugger;

import engine.variable.Variable;

public record DebugStep(
        Variable variableChanged,
        Long newValue
) {}