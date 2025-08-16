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
}
