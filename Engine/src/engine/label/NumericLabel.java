package engine.label;

import engine.argument.Argument;

import java.util.Objects;

public class NumericLabel implements Label, Argument {
    private final int number;

    public NumericLabel(int number) {
        this.number = number;
    }

    @Override
    public String stringRepresentation() {
        return "L" + number;
    }

    @Override
    public Argument toArgument() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NumericLabel that = (NumericLabel) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(number);
    }
}
