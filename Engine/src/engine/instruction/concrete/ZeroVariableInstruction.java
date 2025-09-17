package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.Instruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class ZeroVariableInstruction extends AbstractInstruction {

    public ZeroVariableInstruction(
           Variable variable,
           Label label
    ) {
        super(InstructionData.ZERO_VARIABLE, variable, label);
    }

    @Override
    public Label execute(VariableContext context) {
        context.setVariableValue(getVariable(), 0);
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        return getVariable().stringRepresentation() + " <- 0";
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(); // no arguments
    }

    @Override
    protected Program getSyntheticExpansion() {
        Label l1 = new NumericLabel(getAvaliableLabelNumber());
        List<Instruction> instructionList = new ArrayList<>();

        /* this instruction has a label on its first expanded instruction, if the original
        also had a label, we need to add NOOP. 2 labels aren't allowed on 1 instruction*/
        if(getLabel() != FixedLabel.EMPTY)
            instructionList.add(new NeutralInstruction(Variable.RESULT, getLabel()));

        instructionList.add(new DecreaseInstruction(getVariable(), l1));
        instructionList.add(new JumpNotZeroInstruction(getVariable(), FixedLabel.EMPTY, l1));

        return new StandardProgram(
                getName() + "Expansion",
                instructionList
        );
    }
}