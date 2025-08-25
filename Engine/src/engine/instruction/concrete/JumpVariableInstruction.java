package engine.instruction.concrete;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractJumpInstruction;
import engine.instruction.InstructionData;
import engine.label.Label;
import engine.program.InstructionLocator;
import engine.variable.Variable;

import java.util.List;

public class JumpVariableInstruction extends AbstractJumpInstruction {
    private final Variable otherVariable;

    public JumpVariableInstruction(Variable variable, Label label, Label targetLabel, Variable otherVariable) {
        this(variable, label, targetLabel, otherVariable, null);
    }

    public JumpVariableInstruction(
           Variable variable,
           Label label,
           Label tagetLabel,
           Variable otherVariable,
           InstructionLocator expanding
    ) {
        super(InstructionData.JUMP_EQUAL_VARIABLE, variable, label, tagetLabel, expanding);
        this.otherVariable = otherVariable;
    }

    @Override
    protected boolean isJump(VariableContext context) {
        return context.getVariableValue(getVariable())
                .equals(context.getVariableValue(otherVariable));
    }

    @Override
    public String stringRepresentation() {
        return "IF " + getVariable().stringRepresentation() +
                " = " + otherVariable.stringRepresentation() +
                " GOTO " + getTargetLabel().stringRepresentation();
    }

    @Override
    public List<Argument> getArguments() {
        return List.of(getTargetLabel(), otherVariable);
    }
}
