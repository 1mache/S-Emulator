package engine.instruction.utility;

import engine.argument.ArgumentType;
import engine.instruction.Instruction;
import engine.loader.ArgumentLabelInfo;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.*;
import java.util.stream.Collectors;

public class Instructions {

    public static List<Variable> extractVariables(Instruction instruction) {
        List<Variable> variables = new ArrayList<>();
        if(instruction.getVariable() != Variable.NO_VAR)
            variables.add(instruction.getVariable());

        variables.addAll(
                instruction.getArguments().stream()
                        .filter(arg -> arg.getArgumentType() == ArgumentType.VARIABLE)
                        .map(arg -> (Variable) arg)
                        .toList()
        );
        return variables;
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
                        .filter(arg -> arg.getArgumentType() == ArgumentType.LABEL)
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
                        .filter(arg -> arg.getArgumentType() == ArgumentType.LABEL)
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

        // collect all input variables that are arguments in the instructions
        Set<Variable> argumentVars =
                instructions.stream()
                        .flatMap(instr -> instr.getArguments().stream())
                        .filter(arg -> arg.getArgumentType() == ArgumentType.VARIABLE)
                        .map(arg -> (Variable) arg)
                        .filter(var -> var.getType() == variableType)
                        .collect(Collectors.toSet());

        // unite the two sets
        Set<Variable> allInputs = new HashSet<>(operatedVars);
        allInputs.addAll(argumentVars);

        // convert to list and sort by lineId
        return allInputs.stream()
                .sorted(Comparator.comparingInt(Variable::getNumber))
                .toList();
    }
}