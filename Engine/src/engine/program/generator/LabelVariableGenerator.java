package engine.program.generator;

import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;
import engine.variable.Variable;

import java.util.Collection;
import java.util.List;

public class LabelVariableGenerator {
    private final int initLabelCounter;
    private final int initVariableCounter;

    private int labelCounter;
    private int variableCounter;

    public LabelVariableGenerator(Collection<Label> usedLabels, Collection<Variable> usedWorkVariables){
        // calculate the initial label counter as one more than the largest NumericLabel number present
        int maxLabelNum = usedLabels.stream()
                .filter(l -> l instanceof NumericLabel)
                .mapToInt(l -> ((NumericLabel) l).getNumber())
                .max()
                .orElse(0); // default to 0 if no NumericLabels found
        initLabelCounter = maxLabelNum + 1;

        // calculate the initial variable counter as one more than the largest Variable number present
        int maxVarNum = usedWorkVariables.stream()
                .mapToInt(Variable::getNumber)
                .max()
                .orElse(0); // default to 0 if none
        initVariableCounter = maxVarNum + 1;

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
