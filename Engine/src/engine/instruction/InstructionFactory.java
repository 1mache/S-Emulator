package engine.instruction;

import engine.function.FunctionReference;
import engine.function.parameter.FunctionParamList;
import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;
import engine.numeric.constant.NumericConstant;
import engine.instruction.concrete.*;
import engine.instruction.exception.InstructionArgumentsException;
import engine.label.Label;
import engine.variable.Variable;

import java.util.List;
import java.util.Map;

public class InstructionFactory {
    public static Instruction createInstruction(
            InstructionData instructionData,
            Variable variable,
            Label instructionLabel,
            List<InstructionArgument> arguments
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
            case ASSIGNMENT -> {
                Variable assignedVariable = (Variable) arguments.getFirst();
                yield new AssignmentInstruction(variable, instructionLabel, assignedVariable);
            }
            case CONSTANT_ASSIGNMENT -> {
                NumericConstant constant = (NumericConstant) arguments.getFirst();
                yield new ConstantAssignmentInstruction(variable, instructionLabel, constant);
            }
            case JUMP_ZERO -> {
                Label target = (Label) arguments.getFirst();
                yield new JumpZeroInstruction(variable, instructionLabel, target);
            }
            case JUMP_EQUAL_CONSTANT -> {
                Label target = (Label) arguments.getFirst();
                NumericConstant constant = (NumericConstant) arguments.get(1);
                yield new JumpConstantInstruction(variable, instructionLabel, target, constant);
            }
            case JUMP_EQUAL_VARIABLE -> {
                Label target = (Label) arguments.getFirst();
                Variable comparedVar = (Variable) arguments.get(1);
                yield new JumpVariableInstruction(variable, instructionLabel, target, comparedVar);
            }
            case QUOTE -> {
                FunctionReference quotedReference = (FunctionReference) arguments.getFirst();
                FunctionParamList params = (FunctionParamList) arguments.get(1);
                yield new QuoteInstruction(variable, instructionLabel, quotedReference, params);
            }
            case JUMP_EQUAL_FUNC -> {
                Label target = (Label) arguments.getFirst();
                FunctionReference funcReference = (FunctionReference) arguments.getFirst();
                FunctionParamList params = (FunctionParamList) arguments.get(1);
                yield new QuoteInstruction(variable, instructionLabel, funcReference, params); // TODO; change to JEF
            }
        };
    }

    private static void checkArguments(List<InstructionArgument> arguments, InstructionData instructionData){
        var neededTypes = instructionData.getArgumentTypes();
        if(neededTypes.size() != arguments.size())
            throw new InstructionArgumentsException("Wrong lineId of arguments in instruction: " +
                                                        instructionData.name());

        List<InstructionArgumentType> givenTypes = arguments.stream()
                .map(InstructionArgument::getArgumentType)
                .toList();

        if(!givenTypes.equals(neededTypes))
            throw new InstructionArgumentsException("Argument types mismatch in instruction: " +
                                                        instructionData.name());
    }

    public static Instruction replaceSymbols(
            Instruction instruction,
            Map<Variable, Variable> variableResolutionMap,
            Map<Label, Label> labelResolutionMap
    ) {
        Variable newVar = variableResolutionMap.getOrDefault(instruction.getVariable(), instruction.getVariable());
        Label newLabel = labelResolutionMap.getOrDefault(instruction.getLabel(), instruction.getLabel());

        return createInstruction(instruction.getData(), newVar, newLabel,
                instruction.getArguments().stream()
                        .map(argument ->
                        switch (argument.getArgumentType()){
                                    case VARIABLE -> variableResolutionMap.getOrDefault((Variable) argument, (Variable) argument);
                                    case LABEL -> labelResolutionMap.getOrDefault((Label) argument, (Label) argument);
                                    case CONSTANT -> argument;
                                    default -> throw new InstructionArgumentsException("Unknown argument type");
                                }
                        )
                        .toList()
        );
    }
}
