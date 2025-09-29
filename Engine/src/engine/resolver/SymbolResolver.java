package engine.resolver;

import engine.expansion.SymbolRegistry;
import engine.function.Function;
import engine.function.FunctionCall;
import engine.function.parameter.FunctionParamList;
import engine.instruction.Instruction;
import engine.instruction.InstructionFactory;
import engine.instruction.utility.Instructions;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.numeric.constant.NumericConstant;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.*;

// for now a one time use object
public class SymbolResolver {
    private record ResolutionContext(
            SymbolRegistry usedSymbols,
            SymbolRegistry ignoredSymbols,
            LabelVariableGenerator symbolGenerator
    ){}

    private final ResolutionContext resolutionContext;
    private final Map<Variable, Variable> variableResolutionMap;
    private final Map<Label, Label> labelResolutionMap;

    private SymbolResolver(ResolutionContext resolutionContext){
        this(resolutionContext, new HashMap<>(), new HashMap<>());
    }

    private SymbolResolver(ResolutionContext resolutionContext,
                          Map<Variable, Variable> variableResolutionMap,
                          Map<Label, Label> labelResolutionMap) {
        this.resolutionContext = resolutionContext;
        this.variableResolutionMap = variableResolutionMap;
        this.labelResolutionMap = labelResolutionMap;
    }

    public static SymbolResolver forInstructionExpansion(
            Instruction expanded,
            SymbolRegistry programUsedSymbols
    ){
        SymbolRegistry ignoredSymbols = new SymbolRegistry(
                Instructions.extractUsedLabels(expanded),
                Instructions.extractVariables(expanded)
        );

        // we dont want those touched
        ignoredSymbols.registerVariable(Variable.RESULT);
        ignoredSymbols.registerLabel(FixedLabel.EXIT);

        var symbolGenerator = new LabelVariableGenerator(
                programUsedSymbols.getLabelsUsed(),
                programUsedSymbols.getVariablesUsed().stream()
                        .filter(variable -> variable.getType() == VariableType.WORK)
                        .toList()
        );

        return new SymbolResolver(new ResolutionContext(programUsedSymbols, ignoredSymbols, symbolGenerator));
    }

    public static SymbolResolver forFunctionExpansion(
            Function quotedFunc,
            SymbolRegistry additionalUsedSymbols,
            List<Variable> inputSubstitutions,
            Variable resultSubstitution,
            Label exitSubstitution
    ){
        SymbolRegistry usedSymbols = new SymbolRegistry(additionalUsedSymbols); // copy, we will add to it
        SymbolRegistry ignoredSymbols = new SymbolRegistry(); // nothing ignored

        usedSymbols.registerVariable(Variable.RESULT);
        usedSymbols.registerLabel(FixedLabel.EXIT);
        usedSymbols.registerVariable(resultSubstitution);
        usedSymbols.registerLabel(exitSubstitution);

        Map<Variable, Variable> variableMappings = new HashMap<>();
        variableMappings.put(Variable.RESULT, resultSubstitution);
        Map<Label, Label> labelMappings = new HashMap<>();
        labelMappings.put(FixedLabel.EXIT, exitSubstitution);

        var quotedFuncInputs = quotedFunc.getInputVariables();

        // map inputs to substitutions
        for (int i = 0; i < inputSubstitutions.size(); i++) {
            var xi = quotedFuncInputs.get(i);
            var zi = inputSubstitutions.get(i);

            usedSymbols.registerVariable(xi);
            usedSymbols.registerVariable(zi);

            variableMappings.put(xi, zi);
        }

        var symbolGenerator = new LabelVariableGenerator(usedSymbols.getLabelsUsed(), usedSymbols.getVariablesUsed());

        return new SymbolResolver(
                new ResolutionContext(usedSymbols, ignoredSymbols, symbolGenerator),
                variableMappings,
                labelMappings
        );
    }

    public List<Instruction> resolveSymbolsCollisions(List<Instruction> instructions) {
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

    // ------------ private: ---------------

    private void resolveLabel(Label label,
                              Map<Label, Label> labelResolutionMap) {

        if(labelResolutionMap.containsKey(label))
            return;
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
            while(usedSymbols.isLabelRegistered(replacement))
                replacement = resolutionContext.symbolGenerator().getNextLabel();

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
            while(usedSymbols.isVariableRegistered(replacement))
                replacement = resolutionContext.symbolGenerator().getNextWorkVariable();

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
                                    case CONSTANT -> argument;
                                    case FUNCTION_REF -> {
                                        FunctionCall funcCall = (FunctionCall) argument;
                                        FunctionParamList replacedParamList = replaceParams(funcCall.getParamList(), variableResolutionMap);
                                        yield new FunctionCall(funcCall.getFunction(), funcCall.getReferralName(), replacedParamList);
                                    }
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
                            if (param instanceof NumericConstant)
                                return param;
                            else if (param instanceof Variable v)
                                return replaceVariable(v, variableResolutionMap);
                            else if (param instanceof FunctionCall funcCall) {
                                // recursively replace params inside the FunctionCall, and create a new FunctionCall with replaced params
                                FunctionParamList replacedParamList = replaceParams(funcCall.getParamList(), variableResolutionMap);
                                return new FunctionCall(funcCall.getFunction(), funcCall.getReferralName(), replacedParamList);
                            }
                            throw new IllegalArgumentException("Unknown param type while replacing symbols.");
                        }
                )
                .toList());
    }
}
