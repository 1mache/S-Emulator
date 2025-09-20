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
    private int lastExpansionDegree = 0;
    private List<ExpansionNode> expansionForest; // every instruction is a tree root so forest
    private final SymbolRegistry originalSymbolRegistry;

    private record ExpansionContext(
            SymbolRegistry usedSymbols,
            SymbolRegistry ignoredSymbols,
            LabelVariableGenerator symbolGenerator
    ){}

    public ProgramExpander(Program program) {
        this.program = program;
        originalSymbolRegistry = new SymbolRegistry(program.getUsedLabels(), program.getWorkVariables());

        expansionForest = new ArrayList<>();
        int lineCount = 0;
        for (var instruction: program.getInstructions()) {
            expansionForest.add(new ExpansionNode(new InstructionReference(instruction, lineCount)));
            lineCount++;
        }
    }

    public Program expand(int degree){
        if(program.getMaxExpansionDegree() < degree)
            throw new IllegalArgumentException(
                    "Program " + program.getName() + " has max expansion degree of "
                    + program.getMaxExpansionDegree() + ". Requested expansion degree: " + degree
            );

        if(degree == lastExpansionDegree) // we have it cached
            return buildProgramFromExpansionNodes(expansionForest);

        Program current = program;
        SymbolRegistry usedSymbols = originalSymbolRegistry;
        LabelVariableGenerator symbolGenerator = new LabelVariableGenerator(program);

        for (int currentDegree = 1; currentDegree <= degree; currentDegree++){
            List<ExpansionNode> expandedNodes = new ArrayList<>();

            final int[] lineCounter = {0};
            for(ExpansionNode instructionNode: expansionForest){
                Instruction instruction = instructionNode.getInstructionRef().instruction();

                SymbolRegistry externalSymbols = new SymbolRegistry(
                        Instructions.extractUsedLabels(instruction),
                        Instructions.extractVariables(instruction)
                );
                ExpansionContext expansionContext = new ExpansionContext(
                        usedSymbols,
                        externalSymbols,
                        symbolGenerator
                );

                instruction.getExpansion().ifPresentOrElse(
                        expansion -> {
                            List<Instruction> expansionWithoutCollisions =
                                    resolveSymbolsCollisions(expansion.getInstructions(),expansionContext);

                            // for each expanded instruction
                            for (Instruction expandedInst: expansionWithoutCollisions){
                                // add it as a child of current expansion node
                                ExpansionNode childNode = instructionNode.addChild(
                                        new InstructionReference(expandedInst, lineCounter[0])
                                );
                                // and add it to the next level expansion forest
                                expandedNodes.add(childNode);
                                lineCounter[0]++;
                            }
                        },
                        //or else
                        () -> {
                            // no expansion so this instruction goes to next level as is
                            expandedNodes.add(instructionNode);
                            lineCounter[0]++;
                        }
                );
            }

            // set current to new expanded program
            current = buildProgramFromExpansionNodes(expandedNodes);

            usedSymbols = new SymbolRegistry(current.getUsedLabels(), current.getWorkVariables());
            symbolGenerator = new LabelVariableGenerator(current);
            expansionForest = expandedNodes;
        }

        lastExpansionDegree = degree;

        return current;
    }

    public List<InstructionReference> getExpansionChainOf(int lineNumber){
        if(lineNumber >= expansionForest.size())
            throw new IndexOutOfBoundsException(
                    "Expanded program has only " + expansionForest.size() + " instructions. "
                    + "Expansion chain for line " + lineNumber + "requested"
            );

        List<InstructionReference> result = new ArrayList<>();

        // we start from the most expanded instruction, going up to its parents
        var currentNode = expansionForest.get(lineNumber).getParent();
        while(currentNode.isPresent()) {
            result.add(currentNode.get().getInstructionRef());
            currentNode = currentNode.get().getParent();
        }

        // reverse it because we need to start from the least expanded downwards
        return result.reversed();
    }

    // -------- private ---------

    private Program buildProgramFromExpansionNodes(List<ExpansionNode> nodes){
        return new StandardProgram(
                program.getName() + "_exp",
                nodes.stream()
                        .map(ExpansionNode::getInstructionRef)
                        .map(InstructionReference::instruction)
                        .toList()
        );
    }

    private List<Instruction> resolveSymbolsCollisions(List<Instruction> instructions,
                                                       ExpansionContext expansionContext) {
        List<Instruction> resolved = new ArrayList<>();

        Map<Variable, Variable> variableResolutionMap = new HashMap<>();
        Map<Label, Label> labelResolutionMap = new HashMap<>();

        for(Instruction instruction : instructions){
            Instructions.extractVariables(instruction).forEach(
                    variable -> resolveVariable(variable, variableResolutionMap, expansionContext)
            );

            Instructions.extractUsedLabels(instruction).forEach(
                    label -> resolveLabel(label, labelResolutionMap, expansionContext)
            );

            Instruction newInstruction = InstructionFactory.replaceSymbols(
                    instruction,variableResolutionMap, labelResolutionMap
            );
            resolved.add(newInstruction);
        }
        return resolved;
    }

    private void resolveLabel(Label label,
                              Map<Label, Label> labelResolutionMap,
                              ExpansionContext expansionContext) {

        if(expansionContext.ignoredSymbols.isLabelRegistered(label))
            return;

        var usedSymbols = expansionContext.usedSymbols;

        if(!usedSymbols.isLabelRegistered(label)){
            usedSymbols.registerLabel(label);
            labelResolutionMap.put(label, label);
        }
        else if(!labelResolutionMap.containsKey(label)){
            Label replacement = expansionContext.symbolGenerator.getNextLabel();
            usedSymbols.registerLabel(replacement);
            labelResolutionMap.put(label, replacement);
        }
    }

    private void resolveVariable(Variable variable,
                                 Map<Variable, Variable> variableResolutionMap,
                                 ExpansionContext expansionContext) {

        if(expansionContext.ignoredSymbols.isVariableRegistered(variable))
            return;

        var usedSymbols = expansionContext.usedSymbols;

        if(!usedSymbols.isVariableRegistered(variable)){
            usedSymbols.registerVariable(variable);
            variableResolutionMap.put(variable, variable);
        }
        else if(!variableResolutionMap.containsKey(variable)){
            Variable replacement = expansionContext.symbolGenerator.getNextWorkVariable();
            usedSymbols.registerVariable(replacement);
            variableResolutionMap.put(variable, replacement);
        }
    }
}
