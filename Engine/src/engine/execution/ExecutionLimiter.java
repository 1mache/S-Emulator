package engine.execution;

import engine.instruction.Instruction;

@FunctionalInterface
public interface ExecutionLimiter {
    boolean breakCheck(Instruction nextInstruction);
}
