package engine.instruction.concrete;

import engine.argument.Argument;
import engine.argument.ConstantArgument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractJumpInstruction;
import engine.instruction.Instruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.InstructionReference;
import engine.program.Program;
import engine.program.ProgramImpl;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class JumpConstantInstruction extends AbstractJumpInstruction {
    private final ConstantArgument constant;

    public JumpConstantInstruction(Variable variable, Label label, Label targetLabel, ConstantArgument constant) {
        this(variable, label, targetLabel, constant,null);
    }

    public JumpConstantInstruction(
           Variable variable,
           Label label,
           Label tagetLabel,
           ConstantArgument constant,
           InstructionReference expanding
    ) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variable, label, tagetLabel, expanding);
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
    public List<Argument> getArguments() {
        return List.of(getTargetLabel(),constant);
    }

    @Override
    protected Program getSyntheticExpansion(int lineNumber) {
        InstructionReference locator = new InstructionReference(this, lineNumber);
        Variable z1 = Variable.createWorkVariable(1);
        Variable z2 = Variable.createWorkVariable(2);
        Label l1 = new NumericLabel(1);
        Label empty = FixedLabel.EMPTY;

        List<Instruction> instructions = new ArrayList<>();
        instructions.add(new AssignmentInstruction(z1, empty, getVariable(), locator));

        for (int i = 0; i < constant.value(); i++) {
            instructions.add(new JumpZeroInstruction(z1, empty, l1, locator));
            instructions.add(new DecreaseInstruction(z1, empty, locator));
        }

        instructions.add(new JumpNotZeroInstruction(z1, empty, l1, locator));
        instructions.add(new GotoLabelInstruction(z2, getTargetLabel(), empty, locator));
        instructions.add(new NeutralInstruction(Variable.RESULT, l1, locator));
        return new ProgramImpl(getName() + "Expansion", instructions);
    }
}
