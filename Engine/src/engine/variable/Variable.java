package engine.variable;

import engine.argument.Argument;
import engine.argument.ArgumentType;

import java.util.Objects;

public class Variable implements Argument {
    private final VariableType type;
    private final long number;

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
        String representation = type.stringRepresentation();
        if (type != VariableType.RESULT) representation = representation + number;
        return representation;
    }

    public VariableType getType() {return type;}

    public long getNumber() {return number;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return number == variable.number && type == variable.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, number);
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.VARIABLE;
    }
}
