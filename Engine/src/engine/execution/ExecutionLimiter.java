package engine.execution;

import engine.instruction.Instruction;

public interface ExecutionLimiter {
    boolean breakCheck(Instruction nextInstruction);
    void update(long cycles);
}
