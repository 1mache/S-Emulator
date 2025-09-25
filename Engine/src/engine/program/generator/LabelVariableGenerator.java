package engine.program.generator;

import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;
import engine.variable.Variable;

import java.util.List;

public class LabelVariableGenerator {
    private final int initLabelCounter;
    private final int initVariableCounter;

    private int labelCounter;
    private int variableCounter;

    public LabelVariableGenerator(List<Label> usedLabels, List<Variable> usedWorkVariables){
        // take the first label and variable lineId available
        if(usedLabels.isEmpty() || usedLabels.equals(List.of(FixedLabel.EXIT)))
            initLabelCounter = 1;
        else if(usedLabels.getLast().equals(FixedLabel.EXIT))
            // we know that EXIT is last if present
            initLabelCounter = ((NumericLabel)usedLabels.get(usedLabels.size()-2)).getNumber() + 1;
        else
            initLabelCounter = ((NumericLabel)usedLabels.getLast()).getNumber() + 1;

        if(usedWorkVariables.isEmpty())
            initVariableCounter = 1;
        else
            initVariableCounter = usedWorkVariables.getLast().getNumber() + 1;

        reset();
    }

    public LabelVariableGenerator(Program contextProgram) {
        this(contextProgram.getUsedLabels(), contextProgram.getWorkVariables());
    }

    public void reset(){
        labelCounter = initLabelCounter;
        variableCounter = initVariableCounter;
    }

    public Variable getNextWorkVariable() {
        return Variable.createWorkVariable(variableCounter++);
    }

    public Label getNextLabel(){
        return new NumericLabel(labelCounter++);
    }
}
