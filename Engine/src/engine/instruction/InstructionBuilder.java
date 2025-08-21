package engine.instruction;

import engine.argument.Argument;
import engine.instruction.concrete.DecreaseInstruction;
import engine.instruction.concrete.IncreaseInstruction;
import engine.instruction.concrete.JumpNotZeroInstruction;
import engine.instruction.concrete.NeutralInstruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class InstructionBuilder {
    private final InstructionData instructionData;
    private final Variable variable;
    private Label label;
    private List<Argument> arguments;

    public InstructionBuilder(InstructionData data, Variable variable) {
        this.instructionData = data;
        this.variable = variable;
        reset();
    }

    public void reset(){
        label = FixedLabel.EMPTY;
        arguments = new ArrayList<>();
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public Instruction build() {
        return switch (instructionData) {
            case INCREASE -> new IncreaseInstruction(variable,label);
            case DECREASE -> new DecreaseInstruction(variable,label);
            case JUMP_NOT_ZERO -> new JumpNotZeroInstruction(variable,label, arguments);
            case NEUTRAL -> new NeutralInstruction(variable,label);
        };
    }
}
