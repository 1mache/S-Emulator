package engine.instruction.utility;

import engine.function.FunctionCall;
import engine.function.parameter.FunctionParamList;
import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;
import engine.instruction.Instruction;
import engine.loader.ArgumentLabelInfo;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Instructions {

    public static List<Variable> extractVariables(Instruction instruction) {
        Set<Variable> variables = new HashSet<>();
        if(instruction.getVariable() != Variable.NO_VAR)
            variables.add(instruction.getVariable());

        variables.addAll(extractVariablesFromArguments(List.of(instruction), VariableType.NONE));

        return variables.stream().sorted(Variable.VARIABLE_COMPARATOR).toList();
    }

    public static List<Variable> extractInputVariables(List<Instruction> instructions) {
        return extractVariablesOfType(instructions, VariableType.INPUT);
    }

    public static List<Variable> extractWorkVariables(List<Instruction> instructions){
        return extractVariablesOfType(instructions, VariableType.WORK);
    }

    public static Map<Label, InstructionReference> extractLabeledInstructions(
            List<Instruction> instructions
    ) {
        Map<Label, InstructionReference> result = new HashMap<>();

        /* populate labeledInstructions with instructionData's of instructions
           with nonempty labels*/
        for(int i = 0; i < instructions.size(); i++){
            Instruction instruction = instructions.get(i);
            if(!instruction.getLabel().equals(FixedLabel.EMPTY))
                result.put(
                        instruction.getLabel(), new InstructionReference(instruction, i)
                );
        }

        return result;
    }

    public static List<Label> extractUsedLabels(Instruction instruction) {
        Set<Label> labels = new HashSet<>();
        if(instruction.getLabel() != FixedLabel.EMPTY)
            labels.add(instruction.getLabel());

        labels.addAll(
                getArgumentLabels(List.of(instruction)).stream()
                        .map(ArgumentLabelInfo::label)
                        .collect(Collectors.toSet())
        );

        return labels.stream().sorted(Label.comparator()).toList();
    }

    public static List<ArgumentLabelInfo> getArgumentLabels(List<Instruction> instructions) {
        return instructions.stream()
                .flatMap(instr -> instr.getArguments().stream()
                        .flatMap(arg -> extractLabelsRecursive(instr.getName(), arg)))
                .distinct()
                .collect(Collectors.toList());
    }

    private static Stream<ArgumentLabelInfo> extractLabelsRecursive(String instructionName, InstructionArgument arg) {
        return switch (arg.getArgumentType()) {
            case LABEL -> Stream.of(new ArgumentLabelInfo(instructionName, (Label) arg));

            case FUNC_PARAM_LIST -> ((FunctionParamList) arg).params().stream()
                    .filter(param -> param instanceof InstructionArgument)
                    .map(param -> (InstructionArgument) param)
                    .flatMap(param -> extractLabelsRecursive(instructionName, param));

            case FUNCTION_REF -> ((FunctionCall) arg).getParamList().params().stream()
                    .filter(param -> param instanceof InstructionArgument)
                    .map(param -> (InstructionArgument) param)
                    .flatMap(param -> extractLabelsRecursive(instructionName, param));
            default -> Stream.empty();
        };
    }


    // ------------ private: ------------

    private static List<Variable> extractVariablesOfType(List<Instruction> instructions, VariableType variableType) {
        // collect variables directly operated by instructions
        Set<Variable> operatedVars =
                instructions.stream()
                        .map(Instruction::getVariable)
                        .filter(var -> var.getType() == variableType)
                        .collect(Collectors.toSet());

        var argumentVars = extractVariablesFromArguments(instructions, variableType);

        // unite the two sets
        Set<Variable> allInputs = new HashSet<>(operatedVars);
        allInputs.addAll(argumentVars);

        // convert to list and sort by lineId
        return allInputs.stream()
                .sorted(Comparator.comparingInt(Variable::getNumber))
                .toList();
    }

    private static Set<Variable> extractVariablesFromArguments(List<Instruction> instructions, VariableType variableType) {
        return instructions.stream()
                .flatMap(instr -> instr.getArguments().stream())
                .flatMap(arg -> extractVariablesRecursive(arg, variableType))
                .collect(Collectors.toSet());
    }

    private static Stream<Variable> extractVariablesRecursive(InstructionArgument arg, VariableType variableType) {
        switch (arg.getArgumentType()) {
            case VARIABLE:
                Variable v = (Variable) arg;
                if (variableType == VariableType.NONE || v.getType() == variableType) {
                    return Stream.of(v);
                } else {
                    return Stream.empty();
                }
            case FUNC_PARAM_LIST:
                // Recursively extract from param list
                return ((FunctionParamList) arg).params().stream()
                        .filter(param -> param instanceof InstructionArgument)
                        .map(param -> (InstructionArgument)param)
                        .flatMap(param -> extractVariablesRecursive(param, variableType));
            case FUNCTION_REF:
                // Recursively extract from function call arguments
                return ((FunctionCall) arg).getParamList().params().stream()
                        .filter(param -> param instanceof InstructionArgument)
                        .map(param -> (InstructionArgument)param)
                        .flatMap(param -> extractVariablesRecursive(param, variableType));
            default:
                return Stream.empty();
        }
    }
}