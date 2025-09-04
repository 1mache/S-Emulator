package engine.label;

public class NumericLabel implements Label {
    private final int number;

    public NumericLabel(int number) {
        this.number = number;
    }

    @Override
    public String stringRepresentation() {
        return "L" + number;
    }

    public int getNumber() {
        return number;
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
