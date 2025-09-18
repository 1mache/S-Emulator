package engine.expansion;

import engine.expansion.tree.ExpansionNode;
import engine.instruction.Instruction;
import engine.instruction.InstructionFactory;
import engine.instruction.utility.InstructionReference;
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
    private List<ExpansionNode> expansionForest;
    private SymbolRegistry programSymbolRegistry;
    private LabelVariableGenerator symbolGenerator;

    public ProgramExpander(Program program) {
        this.program = program;
        programSymbolRegistry = new SymbolRegistry(program.getUsedLabels(), program.getWorkVariables());
        symbolGenerator = new LabelVariableGenerator(program);

        expansionForest = new ArrayList<>();
        int lineCount = 0;
        for (var instruction: program.getInstructions()) {
            expansionForest.add(new ExpansionNode(new InstructionReference(instruction, lineCount)));
            lineCount++;
        }
    }

    public Program getProgram() {
        return program;
    }

    public Program expand(int degree){
        Program current = program;
        List<Instruction> expandedInstructions = new ArrayList<>();

        for (int currentDegree = 1; currentDegree <= degree; currentDegree++){
            List<ExpansionNode> nextLevelExpansionForest = new ArrayList<>();
            expandedInstructions.clear();

            final int[] lineCounter = {0};
            for(var expansionNode: expansionForest){
                Instruction instruction = expansionNode.getInstructionRef().instruction();

                SymbolRegistry externalSymbols = new SymbolRegistry(
                        Instructions.extractUsedLabels(instruction),
                        Instructions.extractVariables(instruction)
                );

                instruction.getExpansion().ifPresentOrElse(
                        expansion -> {
                            var expansionWithoutCollisions =
                                    resolveSymbolsCollisions(expansion.getInstructions(), externalSymbols);

                            expandedInstructions.addAll(
                                    expansionWithoutCollisions
                            );

                            for (var inst: expansionWithoutCollisions){
                                ExpansionNode child = expansionNode.addChild(
                                        new InstructionReference(inst, lineCounter[0])
                                );
                                nextLevelExpansionForest.add(child);
                                lineCounter[0]++;
                            }
                        },
                        //or else
                        () -> {
                            expandedInstructions.add(instruction);
                            nextLevelExpansionForest.add(expansionNode);
                            lineCounter[0]++;
                        }
                );
            }

            // set current to new expanded program
            current = new StandardProgram(
                    program.getName() + "_exp" + currentDegree,
                    List.copyOf(expandedInstructions)
            );

            programSymbolRegistry = new SymbolRegistry(current.getUsedLabels(), current.getWorkVariables());
            symbolGenerator = new LabelVariableGenerator(current);
            expansionForest = nextLevelExpansionForest;
        }

        return current;
    }

    public List<InstructionReference> getExpansionChainOf(int lineNumber){
        if(lineNumber >= expansionForest.size())
            throw new IndexOutOfBoundsException(
                    "Expanded program has only " + expansionForest.size() + " instructions. "
                    + "Expansion chain for line " + lineNumber + "requested"
            );

        List<InstructionReference> result = new ArrayList<>();

        var currentNode = expansionForest.get(lineNumber).getParent();
        while(currentNode.isPresent()){
            result.add(currentNode.get().getInstructionRef());
            currentNode = currentNode.get().getParent();
        }

        // reverse it because we need to start from the least expanded downwards
        return result.reversed();
    }

    // -------- private ---------

    private List<Instruction> resolveSymbolsCollisions(List<Instruction> instructions, SymbolRegistry ignoredSymbols){
        List<Instruction> resolved = new ArrayList<>();

        Map<Variable, Variable> variableResolutionMap = new HashMap<>();
        Map<Label, Label> labelResolutionMap = new HashMap<>();

        for(Instruction instruction : instructions){
            Instructions.extractVariables(instruction).forEach(
                    variable -> resolveVariable(variable, variableResolutionMap, ignoredSymbols)
            );

            Instructions.extractUsedLabels(instruction).forEach(
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
            Label replacement = symbolGenerator.getNextLabel();
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
            Variable replacement = symbolGenerator.getNextWorkVariable();
            programSymbolRegistry.registerVariable(replacement);
            variableResolutionMap.put(variable, replacement);
        }
    }
}
