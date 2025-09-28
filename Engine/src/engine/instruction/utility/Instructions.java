package engine.instruction.utility;

import engine.function.parameter.FunctionParamList;
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

        return variables.stream().toList();
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

    public static List<ArgumentLabelInfo> getArgumentLabels(List<Instruction> instructions) {
        return instructions.stream()
                .flatMap(instr -> instr.getArguments().stream()
                        .filter(arg -> arg.getArgumentType() == InstructionArgumentType.LABEL)
                        .map(arg -> new ArgumentLabelInfo(instr.getName(), (Label) arg)))
                .distinct()
                .toList();
    }

    public static List<Label> extractUsedLabels(Instruction instruction) {
        List<Label> labels = new ArrayList<>();
        if(instruction.getLabel() != FixedLabel.EMPTY)
            labels.add(instruction.getLabel());

        labels.addAll(
                instruction.getArguments().stream()
                        .filter(arg -> arg.getArgumentType() == InstructionArgumentType.LABEL)
                        .map(arg -> (Label) arg)
                        .toList()
        );
        return labels;
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
        return Stream.concat(
                        // variables directly from instruction arguments
                        instructions.stream()
                                .flatMap(instr -> instr.getArguments().stream())
                                .filter(arg -> arg.getArgumentType() == InstructionArgumentType.VARIABLE)
                                .map(arg -> (Variable) arg),

                        // variables inside function param lists of instruction arguments (if exists)
                        instructions.stream()
                                .flatMap(instr -> instr.getArguments().stream())
                                .filter(arg -> arg.getArgumentType() == InstructionArgumentType.FUNC_PARAM_LIST)
                                .flatMap(arg -> ((FunctionParamList) arg).params().stream())
                                .filter(param -> param instanceof Variable)
                                .map(param -> (Variable) param)

                        // TODO: composition params here later
                )
                // if NONE type was passed return everything
                .filter(var -> (variableType == VariableType.NONE) || (var.getType() == variableType))
                .collect(Collectors.toSet());
    }
}