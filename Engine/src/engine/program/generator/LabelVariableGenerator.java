package engine.program.generator;

import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;
import engine.variable.Variable;

import java.util.List;

public class LabelVariableGenerator {
    private int labelCounter;
    private int variableCounter;

    public LabelVariableGenerator(Program program) {
        List<Label> labels = program.getUsedLabels();
        List<Variable> workVariables = program.getWorkVariables();

        // take the first label and variable lineId available
        if(labels.isEmpty() || labels.equals(List.of(FixedLabel.EXIT)))
            labelCounter = 0;
        else if(labels.getLast().equals(FixedLabel.EXIT))
            labelCounter = ((NumericLabel)labels.get(labels.size()-2)).getNumber() + 1;
        else
            labelCounter = ((NumericLabel)labels.getLast()).getNumber() + 1;

        if(workVariables.isEmpty())
            variableCounter = 0;
        else
            variableCounter = workVariables.getLast().getNumber() + 1;
    }

    public Variable getNextWorkVariable() {
        return Variable.createWorkVariable(variableCounter++);
    }

    public Label getNextLabel(){
        return new NumericLabel(labelCounter++);
    }
}
