package engine.instruction;

import engine.argument.Argument;
import engine.argument.ArgumentType;
import engine.instruction.concrete.*;
import engine.instruction.exception.InstructionArgumentsException;
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
        // validates the arguments (should always succeed)
        checkArguments(arguments, instructionData);
        return switch (instructionData){
            case INCREASE ->  new IncreaseInstruction(variable, instructionLabel);
            case DECREASE -> new DecreaseInstruction(variable, instructionLabel);
            case JUMP_NOT_ZERO -> {
                Label target = (Label) arguments.getFirst();
                yield new JumpNotZeroInstruction(variable, instructionLabel, target);
            }
            case NEUTRAL -> new NeutralInstruction(variable, instructionLabel);
            case ZERO_VARIABLE -> new ZeroVariableInstruction(variable, instructionLabel);
            case GOTO_LABEL -> {
                Label target = (Label) arguments.getFirst();
                yield new GotoLabelInstruction(instructionLabel, target);
            }
            case ASSIGNMENT -> null;
            case CONSTANT_ASSIGNMENT -> null;
            case JUMP_ZERO -> null;
            case JUMP_EQUAL_CONSTANT -> null;
            case JUMP_EQUAL_VARIABLE -> null;
        };
    }

    private static void checkArguments(List<Argument> arguments, InstructionData instructionData){
        var neededTypes = instructionData.getArgumentTypes();
        if(neededTypes.size() != arguments.size())
            throw new InstructionArgumentsException("Wrong number of arguments in instruction: " +
                                                        instructionData.name());

        List<ArgumentType> givenTypes = arguments.stream()
                .map(Argument::getArgumentType)
                .toList();

        if(!givenTypes.equals(neededTypes))
            throw new InstructionArgumentsException("Argument types mismatch in instruction: " +
                                                        instructionData.name());
    }
}
