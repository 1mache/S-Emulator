package engine.instruction.concrete;

import engine.execution.InstructionExecutionResult;
import engine.instruction.argument.InstructionArgument;
import engine.numeric.constant.NumericConstant;
import engine.execution.context.RunContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.Instruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class ConstantAssignmentInstruction extends AbstractInstruction {
    private final NumericConstant constant;

    public ConstantAssignmentInstruction(
             Variable variable,
             Label label,
             NumericConstant constant
    ) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variable, label);
        this.constant = constant;
    }

    @Override
    public InstructionExecutionResult execute(RunContext context) {
        context.setVariableValue(getVariable(), constant.value());
        return new InstructionExecutionResult(FixedLabel.EMPTY, staticCycles());
    }

    @Override
    public String stringRepresentation() {
        return getVariable().stringRepresentation() + " <- " + constant.value();
    }

    @Override
    public List<InstructionArgument> getArguments() {
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
