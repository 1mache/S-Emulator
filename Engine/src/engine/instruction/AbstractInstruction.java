package engine.instruction;

import engine.label.Label;
import engine.program.InstructionReference;
import engine.program.Program;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.Optional;

public abstract class AbstractInstruction implements Instruction {
    private final InstructionData data;
    private final Variable variable;
    private final Label label;

    // which instruction am I expanding
    private final InstructionReference expanding; // can be null

    public AbstractInstruction(InstructionData data, Variable variable, Label label) {
        this(data, variable, label, null);
    }
    public AbstractInstruction(InstructionData data, Variable variable, Label label,  InstructionReference expanding)
    {
        this.data = data;
        this.label = label;
        this.variable = variable;
        this.expanding = expanding;
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

    @Override
    public Optional<InstructionReference> getExpanding() {
        return Optional.ofNullable(expanding);
    }

    @Override
    public Optional<Program> getExpansion(int lineNumber, LabelVariableGenerator generator) {
        if(!isSynthetic())
            return Optional.empty();
        
        return Optional.of(getSyntheticExpansion(lineNumber, generator));
    }

    // to be implemented by concrete classes.
    protected Program getSyntheticExpansion(int lineNumber, LabelVariableGenerator generator) {
        // if all instructions implement it this should never happen
        throw new UnsupportedOperationException("Instruction " + getName() + " does not support synthetic expansion.");
    }
}
