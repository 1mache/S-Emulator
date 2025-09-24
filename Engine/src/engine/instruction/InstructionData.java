package engine.instruction;

import engine.instruction.argument.InstructionArgumentType;

import java.util.List;

public enum InstructionData {
    INCREASE           (1, false, List.of()),
    DECREASE           (1, false, List.of()),
    JUMP_NOT_ZERO      (2, false, List.of(InstructionArgumentType.LABEL)),
    NEUTRAL            (0, false, List.of()),
    ZERO_VARIABLE      (1, true, List.of()),
    GOTO_LABEL         (1, true, List.of(InstructionArgumentType.LABEL)),
    ASSIGNMENT         (4, true, List.of(InstructionArgumentType.VARIABLE)),
    CONSTANT_ASSIGNMENT(2, true, List.of(InstructionArgumentType.CONSTANT)),
    JUMP_ZERO          (2, true, List.of(InstructionArgumentType.LABEL)),
    JUMP_EQUAL_CONSTANT(2, true, List.of(InstructionArgumentType.LABEL, InstructionArgumentType.CONSTANT)),
    JUMP_EQUAL_VARIABLE(2, true, List.of(InstructionArgumentType.LABEL, InstructionArgumentType.VARIABLE)),

    // advanced:
    QUOTE              (5, true, List.of(InstructionArgumentType.FUNCTION_REF, InstructionArgumentType.FUNC_PARAM_LIST)),
    JUMP_EQUAL_FUNC    (6, true, List.of(InstructionArgumentType.LABEL, InstructionArgumentType.FUNCTION_REF, InstructionArgumentType.FUNC_PARAM_LIST)),
    ;

    private final long cycles;
    private final boolean isSynthetic;
    private final List<InstructionArgumentType> arguments;
    InstructionData(long cycles, boolean isSynthetic, List<InstructionArgumentType> arguments) {
        this.cycles = cycles;
        this.isSynthetic = isSynthetic;
        this.arguments = arguments;
    }

    public long getCycles(){
        return cycles;
    }
    public boolean isSynthetic(){return isSynthetic;}

    public List<InstructionArgumentType> getArgumentTypes() {
        return arguments;
    }
}
