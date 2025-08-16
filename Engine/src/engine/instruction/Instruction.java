package engine.instruction;

import engine.label.Label;

import java.util.Optional;

public interface Instruction {
    void execute();
    String getName();
    int cycles();
    Label getLabel();
}
