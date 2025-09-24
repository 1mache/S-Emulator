package engine.instruction.concrete;

import engine.instruction.argument.InstructionArgument;
import engine.numeric.constant.NumericConstant;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractJumpInstruction;
import engine.instruction.Instruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class JumpConstantInstruction extends AbstractJumpInstruction {
    private final NumericConstant constant;

    public JumpConstantInstruction(
           Variable variable,
           Label label,
           Label tagetLabel,
           NumericConstant constant
    ) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variable, label, tagetLabel);
        this.constant = constant;
    }

    @Override
    protected boolean isJump(VariableContext context) {
        return context.getVariableValue(getVariable()).equals(constant.value());
    }

    @Override
    public String stringRepresentation() {
        return "IF " + getVariable().stringRepresentation() + " = " + constant.value()
                + " GOTO " + getTargetLabel().stringRepresentation();
    }

    @Override
    public List<InstructionArgument> getArguments() {
        return List.of(getTargetLabel(),constant);
    }

    @Override
    protected Program getSyntheticExpansion() {
        Variable z1 = Variable.createWorkVariable(getAvaliableWorkVarNumber());
        Label l1 = new NumericLabel(getAvaliableLabelNumber());
        Label empty = FixedLabel.EMPTY;

        List<Instruction> instructions = new ArrayList<>();
        instructions.add(new AssignmentInstruction(z1, getLabel(), getVariable()));

        for (int i = 0; i < constant.value(); i++) {
            instructions.add(new JumpZeroInstruction(z1, empty, l1));
            instructions.add(new DecreaseInstruction(z1, empty));
        }

        instructions.add(new JumpNotZeroInstruction(z1, empty, l1));
        instructions.add(new GotoLabelInstruction(empty, getTargetLabel()));
        instructions.add(new NeutralInstruction(Variable.RESULT, l1));
        return new StandardProgram(getName() + "Expansion", instructions);
    }
}
