package engine.program.scanner;

import engine.instruction.Instruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.ProgramImpl;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructionScanner {

    public static List<Variable> extractInputVariables(List<Instruction> instructions) {
        return instructions.stream()
                .map(Instruction::getVariable)
                .filter(var -> var.getType() == VariableType.INPUT)
                .sorted(Comparator.comparingLong(Variable::getNumber))
                .toList();
    }

    public static Map<Label, ProgramImpl.InstructionLocator> extractLabeledInstructions(
            List<Instruction> instructions
    ) {
        Map<Label, ProgramImpl.InstructionLocator> result = new HashMap<>();

        /* populate labeledInstructions with instructionData's of instructions
           with nonempty labels*/
        for(int i = 0; i < instructions.size(); i++){
            Instruction instruction = instructions.get(i);
            if(!instruction.getLabel().equals(FixedLabel.EMPTY))
                result.put(
                        instruction.getLabel(), new ProgramImpl.InstructionLocator(instruction, i)
                );
        }

        return result;
    }

    public static List<Label> extractUsedLabels(
            Map<Label, ProgramImpl.InstructionLocator> labeledInstructions
    ) {
        return labeledInstructions.keySet().stream()
                .sorted(Label.comparator())
                .toList();
    }
}