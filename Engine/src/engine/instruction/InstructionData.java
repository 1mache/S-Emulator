package engine.instruction;

public enum InstructionData {
    INCREASE(1, false),
    DECREASE(1, false),
    JUMP_NOT_ZERO(2, false),
    NEUTRAL(0, false),
    ZERO_VARIABLE(1, true),
    GOTO_LABEL(1, true)
    ;

    private final int cycles;
    private final boolean isSynthetic;
    InstructionData(int cycles, boolean isSynthetic) {
        this.cycles = cycles;
        this.isSynthetic = isSynthetic;
    }

    public int getCycles(){
        return cycles;
    }
    public boolean isSynthetic(){return isSynthetic;}
}
