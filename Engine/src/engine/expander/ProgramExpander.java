package engine.expander;

import engine.instruction.Instruction;
import engine.instruction.InstructionFactory;
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
    private SymbolRegistry registry;
    private LabelVariableGenerator generator;

    public ProgramExpander(Program program) {
        this.program = program;
        registry = new SymbolRegistry(program);
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
                registry.setExternalSymbols(instruction); // allow external symbols

                instruction.getExpansion().ifPresentOrElse(
                        expansion -> expandedInstructions.addAll(
                                resolveSymbolsCollisions(expansion.getInstructions())
                        ),
                        () -> expandedInstructions.add(instruction)
                );
                registry.clearExternalSymbols(); // clear allowed symbols
            }

            // set current to new expanded program
            current = new StandardProgram(
                    program.getName() + "_exp" + currentDegree,
                    List.copyOf(expandedInstructions)
            );
        }

        return current;
    }

    private List<Instruction> resolveSymbolsCollisions(List<Instruction> instructions){
        List<Instruction> resolved = new ArrayList<>();

        Map<Variable, Variable> variableResolutionMap = new HashMap<>();
        Map<Label, Label> labelResolutionMap = new HashMap<>();

        for(Instruction instruction : instructions){
            Label label = instruction.getLabel();
            resolveLabel(label, labelResolutionMap);

            Variable variable = instruction.getVariable();
            resolveVariable(variable, variableResolutionMap);

            for(var arg : instruction.getArguments()){
                if(arg instanceof Variable v){
                    resolveVariable(v, variableResolutionMap);
                } else if(arg instanceof Label l){
                    resolveLabel(l, labelResolutionMap);
                }
            }

            Instruction newInstruction = InstructionFactory.replaceSymbols(
                    instruction,variableResolutionMap, labelResolutionMap
            );
            resolved.add(newInstruction);
        }
        return resolved;
    }

    private void resolveLabel(Label label, Map<Label, Label> labelResolutionMap) {
        if(!registry.isLabelOccupied(label)){ // label doesnt collide
            registry.registerLabel(label); // register it from now
        }
        else if(!labelResolutionMap.containsKey(label)){ // label is occupied and we haven't found a replacement yet
            Label newLabel = generator.getNextLabel(); // generate new one
            registry.registerLabel(newLabel); // register it as occupied
            labelResolutionMap.put(label, newLabel);
        }
    }

    private void resolveVariable(Variable variable, Map<Variable, Variable> variableResolutionMap) {
        if(registry.isVariableOccupied(variable)){
            registry.registerVariable(variable);
        }
        else if(!variableResolutionMap.containsKey(variable)){
            Variable newVar = generator.getNextWorkVariable();
            registry.registerVariable(newVar);
            variableResolutionMap.put(variable, newVar);
        }
    }
}
