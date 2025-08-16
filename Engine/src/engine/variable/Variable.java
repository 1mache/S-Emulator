package engine.variable;

public class Variable {
    private final VariableType type;
    private final int number;

    private Variable(VariableType type, int number) {
        this.type = type;
        this.number = number;
    }

    // only one variable of type RESULT is allowed in the program
    public static final Variable RESULT = new Variable(VariableType.RESULT, 0);

    public static Variable createInputVariable(int number) {
        return new Variable(VariableType.INPUT, number);
    }

    public static Variable createWorkVariable(int number) {
        return new Variable(VariableType.WORK, number);
    }

    public String stringRepresentation() {
        return type.stringRepresentation() + number;
    }

    public VariableType getType() {
        return type;
    }
}
