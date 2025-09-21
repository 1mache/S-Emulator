package engine.api.dto.debug;

import engine.debugger.VariableChange;

public final class VariableChangePeek {
    private final String variable;
    private final Long oldValue;
    private final Long newValue;

    public VariableChangePeek(String variable, Long oldValue, Long newValue) {
        this.variable = variable;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public VariableChangePeek(VariableChange change) {
        variable = change.variable().stringRepresentation();
        oldValue = change.oldValue();
        newValue = change.newValue();
    }

    public String variable() {
        return variable;
    }

    public Long oldValue() {
        return oldValue;
    }

    public Long newValue() {
        return newValue;
    }
}
