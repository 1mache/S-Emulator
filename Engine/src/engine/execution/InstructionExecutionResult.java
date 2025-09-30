package engine.execution;

import engine.label.Label;

public record InstructionExecutionResult(Label jumpTo, long cycles) {
}
