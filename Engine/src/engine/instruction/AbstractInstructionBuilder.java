package engine.instruction;

import engine.argument.Argument;
import engine.instruction.concrete.DecreaseInstruction;
import engine.instruction.concrete.IncreaseInstruction;
import engine.instruction.concrete.JumpNotZeroInstruction;
import engine.instruction.concrete.NeutralInstruction;
import engine.label.Label;
import engine.variable.Variable;

import java.util.List;

public class AbstractInstructionBuilder {
    InstructionData instructionData;
    Variable variable;
    Label label;
    List<Argument> arguments;

    public AbstractInstructionBuilder(InstructionData data, Variable variable) {
        this.instructionData = data;
        this.variable = variable;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public AbstractInstruction build() {
        return switch (instructionData) {
            case INCREASE -> new IncreaseInstruction(variable,label);
            case DECREASE -> new DecreaseInstruction(variable,label);
            case JUMP_NOT_ZERO -> new JumpNotZeroInstruction(variable,label, arguments);
            case NEUTRAL -> new NeutralInstruction(variable,label);
        };
    }
}
