package engine.instruction;

import engine.argument.ArgumentType;

import java.util.List;

public enum InstructionData {
    INCREASE(1, false, List.of()),
    DECREASE(1, false, List.of()),
    JUMP_NOT_ZERO(2, false, List.of(ArgumentType.LABEL)),
    NEUTRAL(0, false, List.of()),
    ZERO_VARIABLE(1, true, List.of()),
    GOTO_LABEL(1, true, List.of(ArgumentType.LABEL)),
    ASSIGNMENT(4, true, List.of(ArgumentType.VARIABLE)),
    CONSTANT_ASSIGNMENT(2, true, List.of(ArgumentType.CONSTANT)),
    JUMP_ZERO(2, true, List.of(ArgumentType.LABEL)),
    JUMP_EQUAL_CONSTANT(2, true, List.of(ArgumentType.LABEL, ArgumentType.CONSTANT)),
    JUMP_EQUAL_VARIABLE(2, true, List.of(ArgumentType.LABEL, ArgumentType.VARIABLE))
    ;

    private final long cycles;
    private final boolean isSynthetic;
    private final List<ArgumentType> arguments;
    InstructionData(long cycles, boolean isSynthetic, List<ArgumentType> arguments) {
        this.cycles = cycles;
        this.isSynthetic = isSynthetic;
        this.arguments = arguments;
    }

    public long getCycles(){
        return cycles;
    }
    public boolean isSynthetic(){return isSynthetic;}

    public List<ArgumentType> getArgumentTypes() {
        return arguments;
    }
}
