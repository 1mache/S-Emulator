package engine.instruction;

public enum InstructionData {
    INCREASE(1),
    DECREASE(1),
    JUMP_NOT_ZERO(2),
    NEUTRAL(0)
    ;

    private final int cycles;
    InstructionData(int cycles) {
        this.cycles = cycles;
    }

    public int getCycles(){
        return cycles;
    }
}
