package dto.debug;

import java.util.Optional;

public final class DebugStepPeek {
    private final String variable; // can be null if no variable was changed by the step
    private final Long newValue;

    public DebugStepPeek(String variable, Long newValue) {
        this.variable = variable;
        this.newValue = newValue;
    }

    public Optional<String> variable() {
        return Optional.ofNullable(variable);
    }

    public Long newValue() {
        return newValue;
    }
}
