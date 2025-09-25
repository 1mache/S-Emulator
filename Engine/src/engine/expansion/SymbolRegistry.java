package engine.expansion;

import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SymbolRegistry {
    private final Set<Label> labelsUsed = new HashSet<>();
    private final Set<Variable> variablesUsed = new HashSet<>();

    public SymbolRegistry() {
    }

    public SymbolRegistry(Collection<Label> usedLabels, Collection<Variable> usedVariables){
        usedLabels.forEach(this::registerLabel);
        usedVariables.forEach(this::registerVariable);
    }

    public void registerLabel(Label label) {
        if(label == FixedLabel.EMPTY)
            return;

        labelsUsed.add(label);
    }

    public boolean isLabelRegistered(Label label) {
        return labelsUsed.contains(label);
    }

    public void registerVariable(Variable variable) {
        if(variable == Variable.NO_VAR)
            return;

        variablesUsed.add(variable);
    }

    public boolean isVariableRegistered(Variable variable) {
        return variablesUsed.contains(variable);
    }
}
