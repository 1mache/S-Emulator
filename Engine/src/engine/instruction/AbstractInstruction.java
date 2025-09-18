package engine.instruction;

import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.Optional;

public abstract class AbstractInstruction implements Instruction {
    private final InstructionData data;
    private final Variable variable;
    private final Label label;

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
    public InstructionData getData() {
        return data;
    }

    @Override
    public Optional<Program> getExpansion() {
        if(!isSynthetic())
            return Optional.empty();

        return Optional.of(getSyntheticExpansion());
    }

    // to be implemented by concrete classes.
    protected Program getSyntheticExpansion() {
        // if all instructions implement it, this should never happen
        throw new UnsupportedOperationException("Instruction " + getName() + " does not support synthetic expansion.");
    }

    // -- for expansion --

    protected Optional<Integer> getLabelNumber(){
        if(label instanceof NumericLabel numLabel)
            return Optional.of(numLabel.getNumber());

        return Optional.empty();
    }

    protected Optional<Integer> getVariableNumber(){
        if(variable != Variable.NO_VAR && variable != Variable.RESULT)
            return Optional.of(variable.getNumber());

        return Optional.empty();
    }

    protected int getAvaliableLabelNumber(){
        return getLabelNumber().orElse(0) + 1;
    }

    protected int getAvaliableWorkVarNumber(){
        if(variable.getType() == VariableType.WORK)
           return getVariableNumber().orElse(0) + 1;

        return 1;
    }
}
