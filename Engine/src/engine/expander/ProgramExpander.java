package engine.expander;

import engine.instruction.Instruction;
import engine.instruction.InstructionFactory;
import engine.instruction.utility.Instructions;
import engine.label.Label;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramExpander {
    private final Program program;
    private SymbolRegistry programSymbolRegistry;
    private LabelVariableGenerator generator;

    public ProgramExpander(Program program) {
        this.program = program;
        programSymbolRegistry = new SymbolRegistry(program.getUsedLabels(), program.getWorkVariables());
        generator = new LabelVariableGenerator(program);
    }

    public Program getProgram() {
        return program;
    }

    public Program expand(int degree){
        Program current = program;
        List<Instruction> expandedInstructions = new ArrayList<>();

        for(int currentDegree = 1; currentDegree <= degree; currentDegree++){
            var instructions = current.getInstructions();
            expandedInstructions.clear();

            for(Instruction instruction : instructions){
                SymbolRegistry externalSymbols = new SymbolRegistry(
                        Instructions.extractLabels(instruction),
                        Instructions.extractVariables(instruction)
                );

                instruction.getExpansion().ifPresentOrElse(
                        expansion -> expandedInstructions.addAll(
                                resolveSymbolsCollisions(expansion.getInstructions(), externalSymbols)
                        ),
                        () -> expandedInstructions.add(instruction)
                );
            }

            // set current to new expanded program
            current = new StandardProgram(
                    program.getName() + "_exp" + currentDegree,
                    List.copyOf(expandedInstructions)
            );

            programSymbolRegistry = new SymbolRegistry(current.getUsedLabels(), current.getWorkVariables());
            generator = new LabelVariableGenerator(current);
        }

        return current;
    }

    private List<Instruction> resolveSymbolsCollisions(List<Instruction> instructions, SymbolRegistry ignoredSymbols){
        List<Instruction> resolved = new ArrayList<>();

        Map<Variable, Variable> variableResolutionMap = new HashMap<>();
        Map<Label, Label> labelResolutionMap = new HashMap<>();

        for(Instruction instruction : instructions){
            Instructions.extractVariables(instruction).forEach(
                    variable -> resolveVariable(variable, variableResolutionMap, ignoredSymbols)
            );

            Instructions.extractLabels(instruction).forEach(
                    label -> resolveLabel(label, labelResolutionMap, ignoredSymbols)
            );

            Instruction newInstruction = InstructionFactory.replaceSymbols(
                    instruction,variableResolutionMap, labelResolutionMap
            );
            resolved.add(newInstruction);
        }
        return resolved;
    }

    private void resolveLabel(Label label, Map<Label, Label> labelResolutionMap, SymbolRegistry ignoredSymbols) {
        if(ignoredSymbols.isLabelRegistered(label))
            return;

        if(!programSymbolRegistry.isLabelRegistered(label)){
            programSymbolRegistry.registerLabel(label);
            labelResolutionMap.put(label, label);
        }
        else if(!labelResolutionMap.containsKey(label)){
            Label replacement = generator.getNextLabel();
            programSymbolRegistry.registerLabel(replacement);
            labelResolutionMap.put(label, replacement);
        }
    }

    private void resolveVariable(Variable variable, Map<Variable, Variable> variableResolutionMap, SymbolRegistry ignoredSymbols) {
        if(ignoredSymbols.isVariableRegistered(variable))
            return;

        if(!programSymbolRegistry.isVariableRegistered(variable)){
            programSymbolRegistry.registerVariable(variable);
            variableResolutionMap.put(variable, variable);
        }
        else if(!variableResolutionMap.containsKey(variable)){
            Variable replacement = generator.getNextWorkVariable();
            programSymbolRegistry.registerVariable(replacement);
            variableResolutionMap.put(variable, replacement);
        }
    }
}
