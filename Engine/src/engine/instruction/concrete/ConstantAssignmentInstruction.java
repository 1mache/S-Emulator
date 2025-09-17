package engine.instruction.concrete;

import engine.argument.Argument;
import engine.argument.ConstantArgument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.Instruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class ConstantAssignmentInstruction extends AbstractInstruction {
    private final ConstantArgument constant;

    public ConstantAssignmentInstruction(
             Variable variable,
             Label label,
             ConstantArgument constant
    ) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variable, label);
        this.constant = constant;
    }

    @Override
    public Label execute(VariableContext context) {
        context.setVariableValue(getVariable(), constant.value());
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        return getVariable().stringRepresentation() + " <- " + constant.value();
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(constant);
    }

    @Override
    protected Program getSyntheticExpansion() {
        List<Instruction> instructionList = new ArrayList<>();
        Variable thisVariable = getVariable();

        instructionList.add(new ZeroVariableInstruction(thisVariable, getLabel()));
        for (int i = 0; i < constant.value(); i++)
            instructionList.add(new IncreaseInstruction(thisVariable, FixedLabel.EMPTY));
        
        return new StandardProgram(
                getName() + "Expansion",
                instructionList
        );
    }
}
