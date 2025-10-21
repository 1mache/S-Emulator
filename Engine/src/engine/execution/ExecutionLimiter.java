package engine.execution;

@FunctionalInterface
public interface ExecutionLimiter {
    boolean breakCheck(long cycles);
}
