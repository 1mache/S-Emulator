package engine.program.scanner;

import engine.argument.ArgumentType;
import engine.instruction.Instruction;
import engine.jaxb.loader.ArgumentLabelInfo;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.InstructionReference;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.*;
import java.util.stream.Collectors;

public class InstructionScanner {

    public static List<Variable> extractInputVariables(List<Instruction> instructions) {
        return extractVariables(instructions, VariableType.INPUT);
    }

    public static List<Variable> extractWorkVariables(List<Instruction> instructions){
        return extractVariables(instructions, VariableType.WORK);
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

    public static List<Label> extractUsedLabels(
            Map<Label, InstructionReference> labeledInstructions,
            List<ArgumentLabelInfo> argumentLabels
    ) {
        List<Label> instructionLabels = new ArrayList<>(
                labeledInstructions.keySet().stream()
                .sorted(Label.comparator())
                .toList()
        );

        if(exitIsUsed(argumentLabels))
            instructionLabels.add(FixedLabel.EXIT);

        return instructionLabels;
    }

    private static List<Variable> extractVariables(List<Instruction> instructions, VariableType variableType) {
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

        // convert to list and sort by number
        return allInputs.stream()
                .sorted(Comparator.comparingInt(Variable::getNumber))
                .toList();
    }

    private static boolean exitIsUsed(List<ArgumentLabelInfo> argumentLabels)
    {
        return argumentLabels.stream()
                .map(ArgumentLabelInfo::label)
                .anyMatch(label -> label.equals(FixedLabel.EXIT));
    }
}