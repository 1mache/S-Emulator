package engine.variable;

public class Variable {
    private final VariableType type;
    private final int number;

    public Variable(VariableType type, int number) {
        this.type = type;
        this.number = number;
    }

    public String stringRepresentation() {
        return type.stringRepresentation() + number;
    }

    public VariableType getType() {
        return type;
    }
}
