package engine.instruction;

import engine.argument.Argument;
import engine.argument.ArgumentType;
import engine.argument.ConstantArgument;
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
                yield new GotoLabelInstruction(variable,instructionLabel, target);
            }
            case ASSIGNMENT -> {
                Variable assignedVariable = (Variable) arguments.getFirst();
                yield new AssignmentInstruction(variable, instructionLabel, assignedVariable);
            }
            case CONSTANT_ASSIGNMENT -> {
                ConstantArgument constant = (ConstantArgument) arguments.getFirst();
                yield new ConstantAssignmentInstruction(variable, instructionLabel, constant);
            }
            case JUMP_ZERO -> {
                Label target = (Label) arguments.getFirst();
                yield new JumpZeroInstruction(variable, instructionLabel, target);
            }
            case JUMP_EQUAL_CONSTANT -> {
                Label target = (Label) arguments.getFirst();
                ConstantArgument constant = (ConstantArgument) arguments.get(1);
                yield new JumpConstantInstruction(variable, instructionLabel, target, constant);
            }
            case JUMP_EQUAL_VARIABLE -> {
                Label target = (Label) arguments.getFirst();
                Variable comparedVar = (Variable) arguments.get(1);
                yield new JumpVariableInstruction(variable, instructionLabel, target, comparedVar);
            }
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
