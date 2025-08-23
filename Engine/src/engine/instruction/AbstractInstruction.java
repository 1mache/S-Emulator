package engine.instruction;

import engine.argument.Argument;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInstruction implements Instruction {
    private final InstructionData data;
    private final Variable variable;
    private final Label label;

    public AbstractInstruction(InstructionData data, Variable variable, Label label) {
        this.data = data;
        this.label = label;
        this.variable = variable;
    }

    @Override
    public String getName() {
        return data.name();
    }

    @Override
    public int cycles() {
        return data.getCycles();
    }

    @Override
    public boolean isSynthetic() {
        return data.isSynthetic();
    }

    @Override
    public Variable getVariable() {
        return variable;
    }

    @Override
    public Label getLabel() {
        return label;
    }
}
