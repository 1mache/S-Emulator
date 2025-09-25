package engine.expansion;

import engine.expansion.tree.ExpansionNode;
import engine.instruction.Instruction;
import engine.instruction.utility.InstructionReference;
import engine.instruction.utility.Instructions;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.program.generator.LabelVariableGenerator;
import engine.resolver.ResolutionContext;
import engine.resolver.SymbolResolver;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class ProgramExpander {
    private final Program program;
    private int lastExpansionDegree = 0;
    private List<ExpansionNode> expansionForest; // every instruction is a tree root so forest
    private final SymbolRegistry originalSymbolRegistry;

    public ProgramExpander(Program program) {
        this.program = program;
        originalSymbolRegistry = new SymbolRegistry(program.getUsedLabels(), program.getWorkVariables());

        resetExpansionForest();
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
        resetExpansionForest();
        SymbolRegistry usedSymbols = originalSymbolRegistry;
        LabelVariableGenerator symbolGenerator = new LabelVariableGenerator(program);

        for (int currentDegree = 1; currentDegree <= degree; currentDegree++){
            List<ExpansionNode> expandedNodes = new ArrayList<>();

            final int[] lineCounter = {0};
            for(ExpansionNode instructionNode: expansionForest){
                Instruction instruction = instructionNode.getInstructionRef().instruction();

                // symbols that come from the instruction rather than from it's expansion
                SymbolRegistry ignoredSymbols = new SymbolRegistry(
                        Instructions.extractUsedLabels(instruction),
                        Instructions.extractVariables(instruction)
                );

                // we do not want to replace those or do anything with them
                ignoredSymbols.registerLabel(FixedLabel.EXIT);
                ignoredSymbols.registerVariable(Variable.RESULT);

                ResolutionContext resolutionContext = new ResolutionContext(
                        usedSymbols,
                        ignoredSymbols,
                        symbolGenerator
                );

                var symbolsResolver = new SymbolResolver(resolutionContext);

                instruction.getExpansion().ifPresentOrElse(
                        expansion -> {
                            List<Instruction> expansionWithoutCollisions =
                                    symbolsResolver.resolveExpansionSymbolsCollisions(expansion.getInstructions());

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

    private void resetExpansionForest() {
        expansionForest = new ArrayList<>();
        int lineCount = 0;
        for (var instruction: program.getInstructions()) {
            expansionForest.add(new ExpansionNode(new InstructionReference(instruction, lineCount)));
            lineCount++;
        }
    }

    private Program buildProgramFromExpansionNodes(List<ExpansionNode> nodes){
        return new StandardProgram(
                program.getName() + "_exp",
                nodes.stream()
                        .map(ExpansionNode::getInstructionRef)
                        .map(InstructionReference::instruction)
                        .toList()
        );
    }
}
