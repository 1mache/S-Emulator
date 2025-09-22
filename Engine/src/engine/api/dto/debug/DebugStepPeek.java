package engine.api.dto.debug;

import engine.debugger.DebugStep;
import engine.variable.Variable;

import java.util.Optional;

public final class DebugStepPeek {
    private final String variable; // can be null
    private final Long oldValue;
    private final Long newValue;
    private final Integer cameFromLine;

    public DebugStepPeek(String variable, Long oldValue, Long newValue, Integer cameFromLine) {
        this.variable = variable;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.cameFromLine = cameFromLine;
    }

    public DebugStepPeek(DebugStep step) {
        if(step.variableChanged() == Variable.NO_VAR)
            variable = null;
        else
            variable = step.variableChanged().stringRepresentation();

        oldValue = step.oldValue();
        newValue = step.newValue();
        this.cameFromLine = step.cameFromLine();
    }

    public Optional<String> variable() {
        return Optional.ofNullable(variable);
    }

    public Long oldValue() {
        return oldValue;
    }

    public Long newValue() {
        return newValue;
    }
}
