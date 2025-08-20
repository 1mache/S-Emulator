package engine.instruction;

public enum InstructionData {
    INCREASE(1, 0),
    DECREASE(1, 0),
    JUMP_NOT_ZERO(2, 1),
    NEUTRAL(0, 0)
    ;

    private final int cycles;
    private final int numOfArguments;
    InstructionData(int cycles, int numOfArguments) {
        this.cycles = cycles;
        this.numOfArguments = numOfArguments;
    }

    public int getCycles(){
        return cycles;
    }
    public int getNumOfArguments(){
        return numOfArguments;
    }
}
