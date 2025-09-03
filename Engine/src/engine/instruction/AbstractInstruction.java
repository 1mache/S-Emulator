package engine.instruction;

import engine.label.Label;
import engine.program.Program;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.Optional;

public abstract class AbstractInstruction implements Instruction {
    private final InstructionData data;
    private final Variable variable;
    private final Label label;

    // cached expansion of the instruction
    private Program expansion;

    public AbstractInstruction(InstructionData data, Variable variable, Label label)
    {
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

    @Override
    public Optional<Program> getExpansionInProgram(LabelVariableGenerator generator) {
        if(!isSynthetic())
            return Optional.empty();

        if(expansion == null)
           expansion = Optional.of(getSyntheticExpansion(generator)).get();

        return Optional.of(expansion);
    }

    @Override
    public Optional<Program> getExpansionStandalone() {
        if(!isSynthetic())
            return Optional.empty();

        return Optional.of(getSyntheticExpansion(new LabelVariableGenerator()));
    }

    // to be implemented by concrete classes.
    protected Program getSyntheticExpansion(LabelVariableGenerator generator) {
        // if all instructions implement it, this should never happen
        throw new UnsupportedOperationException("Instruction " + getName() + " does not support synthetic expansion.");
    }
}
