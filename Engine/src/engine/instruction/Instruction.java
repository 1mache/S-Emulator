package engine.instruction;

public interface Instruction {
    void execute();
    String getName();
    int cycles();
}
