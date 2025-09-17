package engine.expander;

import engine.instruction.Instruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.Program;
import engine.variable.Variable;

import java.util.HashSet;
import java.util.Set;

public class SymbolRegistry {
    private final Set<Label> labelsUsed = new HashSet<>();
    private final Set<Label> labelsAllowed = new HashSet<>();
    private final Set<Variable> variablesUsed = new HashSet<>();
    private final Set<Variable> variablesAllowed = new HashSet<>();

    public SymbolRegistry(Program program){
        program.getUsedLabels().forEach(this::registerLabel);
        program.getWorkVariables().forEach(this::registerVariable);
    }

    public void registerLabel(Label label) {
        if(label == FixedLabel.EMPTY)
            return;

        if (!labelsAllowed.contains(label)) {
            labelsUsed.add(label);

            System.out.println("Registered " + label.stringRepresentation());
        }
    }

    public boolean isLabelOccupied(Label label) {
        return labelsUsed.contains(label) && !labelsAllowed.contains(label);
    }

    public void registerVariable(Variable variable) {
        if(variable == Variable.NO_VAR)
            return;

        if(!variablesAllowed.contains(variable)){
            variablesUsed.add(variable);

            System.out.println("Registered " + variable.stringRepresentation());
        }
    }

    public boolean isVariableOccupied(Variable variable) {
        return variablesUsed.contains(variable) && !variablesAllowed.contains(variable);
    }


    public void clearExternalSymbols(){
        labelsAllowed.clear();
        variablesAllowed.clear();
    }

    public void setExternalSymbols(Instruction instruction){
        var label = instruction.getLabel();
        allowLabel(label);

        var variable = instruction.getVariable();
        allowVariable(variable);

        var args = instruction.getArguments();

        for (var arg : args) {
            if(arg instanceof Label l){
                allowLabel(l);
            } else if(arg instanceof Variable v)
                allowVariable(v);
        }
    }

    private void allowLabel(Label label) {
        if(label != FixedLabel.EMPTY)
            labelsAllowed.add(label);
    }

    private void allowVariable(Variable variable) {
        if(variable != Variable.NO_VAR)
            variablesAllowed.add(variable);
    }
}
