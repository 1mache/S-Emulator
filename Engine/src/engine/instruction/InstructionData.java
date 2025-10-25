package engine.instruction;

import engine.instruction.argument.InstructionArgumentType;

import java.util.List;

public enum InstructionData {
    INCREASE           (1, false, List.of(), Architecture.I),
    DECREASE           (1, false, List.of(), Architecture.I),
    JUMP_NOT_ZERO      (2, false, List.of(InstructionArgumentType.LABEL), Architecture.I),
    NEUTRAL            (0, false, List.of(), Architecture.I),
    ZERO_VARIABLE      (1, true, List.of(), Architecture.II),
    GOTO_LABEL         (1, true, List.of(InstructionArgumentType.LABEL), Architecture.II),
    CONSTANT_ASSIGNMENT(2, true, List.of(InstructionArgumentType.CONSTANT), Architecture.II),
    ASSIGNMENT         (4, true, List.of(InstructionArgumentType.VARIABLE), Architecture.III),
    JUMP_ZERO          (2, true, List.of(InstructionArgumentType.LABEL), Architecture.III),
    JUMP_EQUAL_CONSTANT(2, true, List.of(InstructionArgumentType.LABEL, InstructionArgumentType.CONSTANT), Architecture.III),
    JUMP_EQUAL_VARIABLE(2, true, List.of(InstructionArgumentType.LABEL, InstructionArgumentType.VARIABLE), Architecture.III),

    // advanced:
    QUOTE              (5, true, List.of(InstructionArgumentType.FUNCTION_REF, InstructionArgumentType.FUNC_PARAM_LIST), Architecture.IV),
    JUMP_EQUAL_FUNCTION(6, true, List.of(InstructionArgumentType.LABEL, InstructionArgumentType.FUNCTION_REF, InstructionArgumentType.FUNC_PARAM_LIST), Architecture.IV),
    ;

    private final long cycles;
    private final boolean isSynthetic;
    private final List<InstructionArgumentType> arguments;
    private final Architecture architecture;

    InstructionData(long cycles, boolean isSynthetic, List<InstructionArgumentType> arguments, Architecture architecture) {
        this.cycles = cycles;
        this.isSynthetic = isSynthetic;
        this.arguments = arguments;
        this.architecture = architecture;
    }

    public long getCycles(){
        return cycles;
    }
    public boolean isSynthetic(){return isSynthetic;}

    public List<InstructionArgumentType> getArgumentTypes() {
        return arguments;
    }

    public Architecture getArchitecture(){
        return architecture;
    }
}
