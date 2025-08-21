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

    protected final List<Argument> arguments;


    protected AbstractInstruction(InstructionData data, Variable variable) {
        this(data, variable, FixedLabel.EMPTY);
    }

    protected AbstractInstruction(InstructionData data, Variable variable, List<Argument> arguments) {
        this(data, variable, FixedLabel.EMPTY, arguments);
    }

    protected AbstractInstruction(InstructionData data, Variable variable, Label label) {
        this(data, variable, label, new ArrayList<>());
    }

    protected AbstractInstruction(InstructionData data, Variable variable, Label label, List<Argument> arguments) {
        this.data = data;
        this.label = label;
        this.variable = variable;
        this.arguments = arguments;
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
    public Variable getVariable() {
        return variable;
    }

    @Override
    public Label getLabel() {
        return label;
    }
}
