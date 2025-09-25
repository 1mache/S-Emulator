package engine.resolver;

import engine.function.parameter.FunctionParamList;
import engine.instruction.Instruction;
import engine.instruction.InstructionFactory;
import engine.instruction.utility.Instructions;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.numeric.constant.NumericConstant;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolResolver {
    private final ResolutionContext resolutionContext;

    public SymbolResolver(ResolutionContext resolutionContext) {
        this.resolutionContext = resolutionContext;
    }

    public List<Instruction> resolveExpansionSymbolsCollisions(List<Instruction> instructions) {
        Map<Variable, Variable> variableResolutionMap = new HashMap<>();
        Map<Label, Label> labelResolutionMap = new HashMap<>();

        return resolveSymbolsCollisions(instructions, variableResolutionMap, labelResolutionMap);
    }

    public List<Instruction> resolveFunctionSymbolsCollisions(
            List<Instruction> instructions,
            Label exitLabelSubstitution,
            Variable resultSubstitution,
            Map<Variable, Variable> inputsSubstitutions){

        Map<Variable, Variable> variableResolutionMap = new HashMap<>();
        Map<Label, Label> labelResolutionMap = new HashMap<>();

        // those are used by the function (even if not, we have substitutions for them in case)
        resolutionContext.usedSymbols().registerLabel(FixedLabel.EXIT);
        resolutionContext.usedSymbols().registerVariable(Variable.RESULT);

        variableResolutionMap.put(Variable.RESULT, resultSubstitution);
        variableResolutionMap.putAll(inputsSubstitutions);

        labelResolutionMap.put(FixedLabel.EXIT, exitLabelSubstitution);

        return resolveSymbolsCollisions(instructions, variableResolutionMap, labelResolutionMap);
    }

    // ------------ private: ---------------

    private List<Instruction> resolveSymbolsCollisions(List<Instruction> instructions,
                                                       Map<Variable, Variable> variableResolutionMap,
                                                       Map<Label, Label> labelResolutionMap) {
        List<Instruction> resolved = new ArrayList<>();

        for(Instruction instruction : instructions){
            Instructions.extractVariables(instruction).forEach(
                    variable -> resolveVariable(variable, variableResolutionMap)
            );

            Instructions.extractUsedLabels(instruction).forEach(
                    label -> resolveLabel(label, labelResolutionMap)
            );

            Instruction newInstruction = replaceSymbols(
                    instruction,variableResolutionMap, labelResolutionMap
            );
            resolved.add(newInstruction);
        }
        return resolved;
    }

    private void resolveLabel(Label label,
                              Map<Label, Label> labelResolutionMap) {

        if(resolutionContext.ignoredSymbols().isLabelRegistered(label))
            return;

        var usedSymbols = resolutionContext.usedSymbols();

        // unused and unregistered
        if(!usedSymbols.isLabelRegistered(label)){
            usedSymbols.registerLabel(label);
            labelResolutionMap.put(label, label);
        } // used and this is the first time we see it, so find replacement
        else if(!labelResolutionMap.containsKey(label)){
            Label replacement = resolutionContext.symbolGenerator().getNextLabel();
            usedSymbols.registerLabel(replacement);
            labelResolutionMap.put(label, replacement);
        }
    }

    private void resolveVariable(Variable variable,
                                 Map<Variable, Variable> variableResolutionMap) {

        if(resolutionContext.ignoredSymbols().isVariableRegistered(variable))
            return;

        var usedSymbols = resolutionContext.usedSymbols();

        if(!usedSymbols.isVariableRegistered(variable)){
            usedSymbols.registerVariable(variable);
            variableResolutionMap.put(variable, variable);
        }
        else if(!variableResolutionMap.containsKey(variable)){
            Variable replacement = resolutionContext.symbolGenerator().getNextWorkVariable();
            usedSymbols.registerVariable(replacement);
            variableResolutionMap.put(variable, replacement);
        }
    }

    private Instruction replaceSymbols(
            Instruction instruction,
            Map<Variable, Variable> variableResolutionMap,
            Map<Label, Label> labelResolutionMap
    ) {
        Variable newVar = variableResolutionMap.getOrDefault(instruction.getVariable(), instruction.getVariable());
        Label newLabel = labelResolutionMap.getOrDefault(instruction.getLabel(), instruction.getLabel());

        return InstructionFactory.createInstruction(instruction.getData(), newVar, newLabel,
                instruction.getArguments().stream()
                        .map(argument ->
                                switch (argument.getArgumentType()){
                                    case VARIABLE -> replaceVariable((Variable) argument, variableResolutionMap);
                                    case LABEL -> labelResolutionMap.getOrDefault((Label) argument, (Label) argument);
                                    case CONSTANT, FUNCTION_REF -> argument;
                                    case FUNC_PARAM_LIST -> replaceParams((FunctionParamList) argument, variableResolutionMap);
                                }
                        )
                        .toList()
        );
    }

    private Variable replaceVariable(Variable variable, Map<Variable, Variable> variableResolutionMap) {
        return variableResolutionMap.getOrDefault(variable, variable);
    }

    private FunctionParamList replaceParams(FunctionParamList list, Map<Variable, Variable> variableResolutionMap) {
        return new FunctionParamList(list.params().stream()
                .map(
                        param -> {
                            if(param instanceof NumericConstant)
                                return param;
                            else if(param instanceof Variable v)
                                return replaceVariable(v, variableResolutionMap);
                            throw new IllegalArgumentException("Unknown param type while replacing symbols");
                        }
                )
                .toList());
    }
}
