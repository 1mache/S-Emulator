package engine.api.dto;

import engine.variable.Variable;

public record VariableChange(Variable variable, Long oldValue, Long newValue) {}
