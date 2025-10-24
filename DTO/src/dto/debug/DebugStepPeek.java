package dto.debug;

import java.util.Optional;

public final class DebugStepPeek {
    private final String variable; // can be null if no variable was changed by the step
    private final long newValue;
    private final int nextLine;

    public DebugStepPeek(String variable, Long newValue, int nextLine) {
        this.variable = variable;
        this.newValue = newValue;
        this.nextLine = nextLine;
    }

    public Optional<String> variable() {
        return Optional.ofNullable(variable);
    }

    public long newValue() {
        return newValue;
    }

    public int nextLine() {
        return nextLine;
    }
}
