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

    // no program context
    public LabelVariableGenerator(){
        initLabelCounter = 1;
        initVariableCounter = 1;
    }

    public LabelVariableGenerator(Program program) {
        List<Label> labels = program.getUsedLabels();
        List<Variable> workVariables = program.getWorkVariables();

        // take the first label and variable lineId available
        if(labels.isEmpty() || labels.equals(List.of(FixedLabel.EXIT)))
            initLabelCounter = 1;
        else if(labels.getLast().equals(FixedLabel.EXIT))
            // we know that EXIT is last if present
            initLabelCounter = ((NumericLabel)labels.get(labels.size()-2)).getNumber() + 1;
        else
            initLabelCounter = ((NumericLabel)labels.getLast()).getNumber() + 1;

        if(workVariables.isEmpty())
            initVariableCounter = 1;
        else
            initVariableCounter = workVariables.getLast().getNumber() + 1;

        reset();
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
