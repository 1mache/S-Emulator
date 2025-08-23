package engine.instruction;

import engine.argument.Argument;
import engine.instruction.concrete.*;
import engine.label.Label;
import engine.variable.Variable;

import java.util.List;

public class InstructionFactory {
    public static Instruction createInstruction(
            InstructionData instructionData,
            Variable variable,
            Label instructionLabel,
            List<Argument> arguments
    ){
        return switch (instructionData){
            case INCREASE ->  new IncreaseInstruction(variable, instructionLabel);
            case DECREASE -> new DecreaseInstruction(variable, instructionLabel);
            case JUMP_NOT_ZERO -> {
                Label target = (Label) arguments.getFirst();
                yield new JumpNotZeroInstruction(variable, instructionLabel, target);
            }
            case NEUTRAL -> new NeutralInstruction(variable, instructionLabel);
            case ZERO_VARIABLE -> new ZeroVariable(variable, instructionLabel);
            case GOTO_LABEL -> {
                Label target = (Label) arguments.getFirst();
                yield new GotoLabel(instructionLabel, target);
            }
        };
    }

    //TODO: arguments check
}
