package engine.instruction;

import engine.label.Label;

public interface Instruction {
    void execute();
    String getName();
    int cycles();
    Label getLabel();
}
