package engine.instruction;

// record that represents instruction and its ordinal id in the program
public record InstructionIdentifier(Instruction instruction, int pcId) {
    public static final InstructionIdentifier EMPTY =
            new InstructionIdentifier(null, 0);
}