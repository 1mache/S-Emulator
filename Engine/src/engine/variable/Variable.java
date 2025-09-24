package engine.variable;

import engine.execution.context.RunContext;
import engine.function.parameter.FunctionParam;
import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;

import java.util.Comparator;
import java.util.Objects;

public class Variable implements InstructionArgument, FunctionParam {
    private final VariableType type;
    private final int number;

    private Variable(VariableType type, int number) {
        this.type = type;
        this.number = number;
    }

    // only one variable of type RESULT is allowed in the program
    public static final Variable RESULT = new Variable(VariableType.RESULT, 0);
    public static final Variable NO_VAR =  new Variable(VariableType.NONE, 0);
    public static Variable createInputVariable(int number) {
        return new Variable(VariableType.INPUT, number);
    }

    public static Variable createWorkVariable(int number) {
        return new Variable(VariableType.WORK, number);
    }

    public static final Comparator<Variable> VARIABLE_COMPARATOR = (v1, v2) -> {
        // RESULT always first
        if (v1.getType() == VariableType.RESULT && v2.getType() != VariableType.RESULT) return -1;
        if (v2.getType() == VariableType.RESULT && v1.getType() != VariableType.RESULT) return 1;

        // INPUT before WORK
        if (v1.getType() == VariableType.INPUT && v2.getType() == VariableType.WORK) return -1;
        if (v1.getType() == VariableType.WORK && v2.getType() == VariableType.INPUT) return 1;

        // NONE goes last
        if (v1.getType() == VariableType.NONE && v2.getType() != VariableType.NONE) return 1;
        if (v2.getType() == VariableType.NONE && v1.getType() != VariableType.NONE) return -1;

        // Same type → compare by number
        if (v1.getType() == v2.getType()) {
            return Integer.compare(v1.getNumber(), v2.getNumber());
        }

        // Fallback to type name just in case
        return v1.getType().compareTo(v2.getType());
    };

    public String stringRepresentation() {
        String representation = type.stringRepresentation();
        if (type != VariableType.RESULT) representation = representation + number;
        return representation;
    }

    public VariableType getType() {return type;}

    public int getNumber() {return number;}

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
    public InstructionArgumentType getArgumentType() {
        return InstructionArgumentType.VARIABLE;
    }

    @Override
    public Long eval(RunContext context) {
        return context.getVariableValue(this);
    }
}
