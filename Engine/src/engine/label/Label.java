package engine.label;

import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;

import java.util.Comparator;

public interface Label extends InstructionArgument {
    String stringRepresentation();

    static Comparator<Label> comparator() {
        return (a, b) -> {
            if (a.equals(b)) return 0;

            // Rule 1: EMPTY is always smallest
            if (a == FixedLabel.EMPTY) return -1;
            if (b == FixedLabel.EMPTY) return 1;

            // Rule 2: EXIT is always largest
            if (a == FixedLabel.EXIT) return 1;
            if (b == FixedLabel.EXIT) return -1;

            // Rule 3: Both numeric labels → compare their numbers
            if (a instanceof NumericLabel na && b instanceof NumericLabel nb) {
                return Integer.compare(na.getNumber(), nb.getNumber());
            }

            // Safety net: if mixed types not covered
            return a.toString().compareTo(b.toString());
        };
    }

    @Override
    default InstructionArgumentType getArgumentType() {
        return InstructionArgumentType.LABEL;
    }
}
