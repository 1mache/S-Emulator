package engine.expander;

import engine.label.FixedLabel;
import engine.label.Label;
import engine.variable.Variable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SymbolRegistry {
    private final Set<Label> labelsUsed = new HashSet<>();
    private final Set<Variable> variablesUsed = new HashSet<>();

    public SymbolRegistry(Collection<Label> usedLabels, Collection<Variable> usedVariables){
        usedLabels.forEach(this::registerLabel);
        usedVariables.forEach(this::registerVariable);
    }

    public void registerLabel(Label label) {
        if(label == FixedLabel.EMPTY || label == FixedLabel.EXIT)
            return;

        if(!labelsUsed.contains(label)){ //DEBUG
            System.out.println("Registered " + label.stringRepresentation());
        }

        labelsUsed.add(label);
    }

    public boolean isLabelRegistered(Label label) {
        return labelsUsed.contains(label);
    }

    public void registerVariable(Variable variable) {
        if(variable == Variable.NO_VAR || variable == Variable.RESULT)
            return;

        if(!variablesUsed.contains(variable)) { //DEBUG
            System.out.println("Registered " + variable.stringRepresentation());
        }
        variablesUsed.add(variable);
    }

    public boolean isVariableRegistered(Variable variable) {
        return variablesUsed.contains(variable);
    }
}
