package engine.instruction;

import engine.label.Label;
import engine.variable.Variable;

public interface Instruction {
    void execute();
    String getName();
    int cycles();
    Variable getVariable();
    Label getLabel();
}
