package engine.debugger;

import engine.variable.Variable;

public record VariableChange(Variable variable, Long oldValue, Long newValue) {}
